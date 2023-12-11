package dev.sdex.currencyexchanger.domain.usecase

import dev.sdex.currencyexchanger.domain.model.Balance
import dev.sdex.currencyexchanger.domain.model.BalanceChange
import dev.sdex.currencyexchanger.domain.model.ExchangeRate
import dev.sdex.currencyexchanger.domain.model.ExchangeTransaction
import dev.sdex.currencyexchanger.domain.model.ExchangeTransactionResult
import dev.sdex.currencyexchanger.domain.provider.ExchangeRulesProvider
import java.math.BigDecimal
import java.math.MathContext

class ExchangeUseCase(
    private val exchangeRulesProvider: ExchangeRulesProvider,
    private val getExchangeRateUseCase: GetExchangeRateUseCase,
) {

    operator fun invoke(
        exchangeRates: List<ExchangeRate>,
        balance: List<Balance>,
        transaction: ExchangeTransaction,
    ): ExchangeTransactionResult {
        val rate = getExchangeRateUseCase(
            exchangeRates = exchangeRates,
            sellCurrency = transaction.sellCurrency,
            buyCurrency = transaction.buyCurrency,
        )
        val buyAmount = transaction.amount.multiply(rate, MathContext.DECIMAL64)
        val transactionFee = getTransactionFee(transaction)
        val sellCurrencyBalance = balance.first { it.currency == transaction.sellCurrency }
        val sellBalanceChange = transaction.amount + transactionFee
        if (sellBalanceChange > sellCurrencyBalance.amount) {
            return ExchangeTransactionResult(
                transaction = transaction,
                transactionFee = transactionFee,
                isProcessed = false,
                change = BalanceChange(
                    sellCurrency = transaction.sellCurrency,
                    sellBalanceChange = BigDecimal.ZERO,
                    buyCurrency = transaction.buyCurrency,
                    buyBalanceChange = BigDecimal.ZERO,
                ),
            )
        }
        return ExchangeTransactionResult(
            transaction = transaction,
            transactionFee = transactionFee,
            isProcessed = true,
            change = BalanceChange(
                transaction.sellCurrency,
                -sellBalanceChange,
                transaction.buyCurrency,
                buyAmount,
            )
        )
    }

    private fun getTransactionFee(transaction: ExchangeTransaction): BigDecimal {
        for (rule in exchangeRulesProvider.rules) {
            val result = rule.process(transaction)
            if (result.fee != null) {
                return result.fee
            }
        }
        return BigDecimal.ZERO
    }
}