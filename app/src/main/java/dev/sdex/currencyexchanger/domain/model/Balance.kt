package dev.sdex.currencyexchanger.domain.model

data class Balance(
    val currency: String,
    val amount: Double,
)