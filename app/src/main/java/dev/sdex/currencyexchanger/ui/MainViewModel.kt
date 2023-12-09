package dev.sdex.currencyexchanger.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sdex.currencyexchanger.domain.model.Balance
import dev.sdex.currencyexchanger.domain.model.ExchangeRate
import dev.sdex.currencyexchanger.domain.usecase.GetExchangeRate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

data class ExchangeCurrencyState(
    val balance: List<Balance>,
    val availableCurrencies: List<String> = emptyList(),
    val allCurrencies: List<String> = emptyList(),
    val exchangeRates: List<ExchangeRate> = emptyList(),
    val receiveAmount: String = "0.00",
) {

    /*fun getBalanceSorted(): List<Balance> {
        return balance.sortedWith(compareBy({ it.amount }, { it.currency }))
    }*/
}

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
}

class MainViewModel(
    private val getExchangeRate: GetExchangeRate,
) : ViewModel() {

    private var _uiState = MutableStateFlow(
        ExchangeCurrencyState(balance = emptyList(), exchangeRates = emptyList())
    )
    val uiState: StateFlow<ExchangeCurrencyState> = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(
                balance = listOf(
                    Balance(
                        currency = "EUR",
                        amount = 1000.0,
                    )
                ),
                availableCurrencies = listOf("EUR"),
            )
        }
        getExchangeRate().onEach { result ->
            _uiState.update {
                it.copy(
                    exchangeRates = result.data ?: emptyList(),
                    allCurrencies = result.data?.sortedBy { it.currency }?.map { it.currency }
                        ?: emptyList(),
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.Exchange -> {
                // TODO apply rules
                //  check is enough money
                //  update balance
            }

            is UIEvent.GetExchangeRate -> {
                if (uiEvent.sellAmount.isEmpty() ||
                    uiEvent.sellCurrency.isEmpty() ||
                    uiEvent.buyCurrency.isEmpty()
                ) {
                    return
                }
                uiState.value.exchangeRates
                    .firstOrNull { it.currency == uiEvent.buyCurrency }
                    ?.let { exchangeRate ->
                        _uiState.update {
                            it.copy(
                                receiveAmount = "%.2f".format(uiEvent.sellAmount.toDouble() * exchangeRate.rate),
                            )
                        }
                    }
            }
        }
    }
}