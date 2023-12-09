package dev.sdex.currencyexchanger.domain.rules

interface ExchangeRule {

    fun process(
        transactionId: Long,
        sellCurrency: String,
        buyCurrency: String,
        amount: Double,
        rate: Double
    ): Double
}