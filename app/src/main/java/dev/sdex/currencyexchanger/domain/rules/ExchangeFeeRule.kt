package dev.sdex.currencyexchanger.domain.rules

import dev.sdex.currencyexchanger.domain.model.ExchangeTransaction
import java.math.BigDecimal

interface ExchangeFeeRule {

    fun process(
        transaction: ExchangeTransaction
    ): BigDecimal
}