package dev.sdex.currencyexchanger.ui

import java.math.BigDecimal
import java.math.RoundingMode

fun formatDecimal(decimal: BigDecimal): String =
    decimal.setScale(2, RoundingMode.CEILING).toPlainString()