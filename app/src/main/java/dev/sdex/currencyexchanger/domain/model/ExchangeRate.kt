package dev.sdex.currencyexchanger.domain.model

data class ExchangeRate(
    val currency: String,
    val rate: Double,
)