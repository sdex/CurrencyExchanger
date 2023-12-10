package dev.sdex.currencyexchanger.domain.model

import java.math.BigDecimal

data class ExchangeRate(
    val currency: String,
    val rate: BigDecimal,
)