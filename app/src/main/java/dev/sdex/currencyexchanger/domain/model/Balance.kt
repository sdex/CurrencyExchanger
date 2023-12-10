package dev.sdex.currencyexchanger.domain.model

import java.math.BigDecimal

data class Balance(
    val currency: String,
    val amount: BigDecimal,
)