package dev.sdex.currencyexchanger.domain.rules

import dev.sdex.currencyexchanger.domain.model.ExchangeTransaction

interface ExchangeFeeRule {

    fun process(
        transaction: ExchangeTransaction
    ): ExchangeFeeRuleResult
}