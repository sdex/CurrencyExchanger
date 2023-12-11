package dev.sdex.currencyexchanger.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sdex.currencyexchanger.domain.model.Balance
import dev.sdex.currencyexchanger.domain.model.ExchangeRate
import dev.sdex.currencyexchanger.domain.model.ExchangeTransaction
import dev.sdex.currencyexchanger.domain.model.ExchangeTransactionResult
import dev.sdex.currencyexchanger.domain.usecase.ExchangeUseCase
import dev.sdex.currencyexchanger.domain.usecase.GetExchangeRateUseCase
import dev.sdex.currencyexchanger.domain.usecase.GetExchangeRatesUseCase
import dev.sdex.currencyexchanger.domain.usecase.UpdateBalanceUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.math.BigDecimal

private const val BASE_CURRENCY = "EUR"

data class ExchangeCurrencyState(
    val balance: List<Balance> = emptyList(),
    val availableCurrencies: List<String> = emptyList(),
    val allCurrencies: List<String> = emptyList(),
    val exchangeRates: List<ExchangeRate> = emptyList(),
    val receiveAmount: String = "0.00",
    val message: String? = null,
    val transactionResult: ExchangeTransactionResult? = null,
)

sealed class UIEvent {
    data class Exchange(
        val sellCurrency: String,
        val sellAmount: String,
        val buyCurrency: String,
    ) : UIEvent()

    data class GetExchangeRate(
        val sellCurrency: String,
        val sellAmount: String,
        val buyCurrency: String,
    ) : UIEvent()

    data object CloseResultDialog : UIEvent()
}

sealed class Action {
    data object ClearInputField : Action()
}

class MainViewModel(
    private val getExchangeRatesUseCase: GetExchangeRatesUseCase,
    private val exchangeUseCase: ExchangeUseCase,
    private val getExchangeRateUseCase: GetExchangeRateUseCase,
    private val updateBalanceUseCase: UpdateBalanceUseCase,
) : ViewModel() {

    private var _uiState = MutableStateFlow(ExchangeCurrencyState())
    val uiState: StateFlow<ExchangeCurrencyState> = _uiState.asStateFlow()

    private val _actions = MutableSharedFlow<Action>(extraBufferCapacity = 1)
    val actions: SharedFlow<Action> = _actions.asSharedFlow()

    init {
        _uiState.update {
            it.copy(
                balance = listOf(
                    Balance(
                        currency = BASE_CURRENCY,
                        amount = BigDecimal("1000"),
                    )
                ),
                availableCurrencies = listOf(BASE_CURRENCY),
            )
        }
        getExchangeRatesUseCase().onEach { result ->
            _uiState.update {
                it.copy(
                    exchangeRates = result.data?.rates ?: emptyList(),
                    allCurrencies = it.allCurrencies.ifEmpty {
                        result.data?.rates?.sortedBy { it.currency }?.map { it.currency }
                            ?: emptyList()
                    },
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.Exchange -> {
                processExchange(uiEvent)
            }

            is UIEvent.GetExchangeRate -> {
                getExchangeRate(uiEvent)
            }

            UIEvent.CloseResultDialog -> {
                closeResultDialog()
            }
        }
    }

    private fun closeResultDialog() {
        _uiState.update {
            it.copy(
                transactionResult = null,
            )
        }
    }

    private fun getExchangeRate(uiEvent: UIEvent.GetExchangeRate) {
        val sellCurrency = uiEvent.sellCurrency
        val buyCurrency = uiEvent.buyCurrency
        if (sellCurrency.isEmpty() ||
            buyCurrency.isEmpty() ||
            uiState.value.exchangeRates.isEmpty()
        ) {
            return
        }
        if (uiEvent.sellAmount.isEmpty()) {
            _uiState.update {
                it.copy(
                    receiveAmount = "0.00",
                    message = null,
                )
            }
            return
        }
        Timber.d(
            "GetExchangeRate: " +
                    "sellCurrency: $sellCurrency, " +
                    "sellAmount: ${uiEvent.sellAmount}, " +
                    "buyCurrency: $buyCurrency"
        )
        val sellAmount = BigDecimal(uiEvent.sellAmount)
        val rate = getExchangeRateUseCase(
            exchangeRates = uiState.value.exchangeRates,
            sellCurrency = sellCurrency,
            buyCurrency = buyCurrency,
        )
        _uiState.update {
            it.copy(
                receiveAmount = formatDecimal(sellAmount * rate),
                message = null,
            )
        }
    }

    private fun processExchange(uiEvent: UIEvent.Exchange) {
        val buyCurrency = uiEvent.buyCurrency
        val sellCurrency = uiEvent.sellCurrency
        Timber.d(
            "Exchange: " +
                    "sellCurrency: $sellCurrency, " +
                    "sellAmount: ${uiEvent.sellAmount}, " +
                    "buyCurrency: $buyCurrency"
        )
        if (uiEvent.sellAmount.toDoubleOrNull() == null ||
            uiState.value.exchangeRates.isEmpty() ||
            sellCurrency == buyCurrency
        ) {
            return
        }
        val sellAmount = BigDecimal(uiEvent.sellAmount)
        if (sellAmount == BigDecimal.ZERO) {
            return
        }

        if (!hasEnoughFunds(sellCurrency, sellAmount)) {
            _uiState.update {
                it.copy(
                    message = "Not enough funds on balance",
                )
            }
            return
        }

        val transactionResult = exchangeUseCase(
            uiState.value.exchangeRates,
            uiState.value.balance,
            ExchangeTransaction(
                sellCurrency = sellCurrency,
                buyCurrency = buyCurrency,
                amount = sellAmount,
            )
        )
        if (transactionResult.isProcessed) {
            _uiState.update {
                val newBalance = updateBalanceUseCase(
                    it.balance,
                    transactionResult,
                )
                it.copy(
                    balance = newBalance,
                    availableCurrencies = newBalance.map { it.currency },
                    message = null,
                    receiveAmount = "0.00",
                    transactionResult = transactionResult,
                )
            }
            _actions.tryEmit(Action.ClearInputField)
        } else {
            _uiState.update {
                it.copy(
                    message = "Not enough funds on balance to pay " +
                            "${transactionResult.transactionFee} " +
                            "${transactionResult.transaction.sellCurrency} fee",
                )
            }
        }
    }

    private fun hasEnoughFunds(sellCurrency: String, sellAmount: BigDecimal): Boolean {
        val sellCurrencyBalance =
            uiState.value.balance.first { it.currency == sellCurrency }
        return sellAmount <= sellCurrencyBalance.amount
    }
}