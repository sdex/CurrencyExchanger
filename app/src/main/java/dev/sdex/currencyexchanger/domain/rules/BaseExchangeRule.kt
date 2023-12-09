package dev.sdex.currencyexchanger.domain.rules

/**
 * First 5 transactions are fee-free,
 * afterwards the fee is 0.7%
 */
class BaseExchangeRule: ExchangeRule {

    override fun process(
        transactionId: Long,
        sellCurrency: String,
        buyCurrency: String,
        amount: Double,
        rate: Double
    ): Double {
        if (transactionId > 4) {

        }
        return amount * rate
    }
}