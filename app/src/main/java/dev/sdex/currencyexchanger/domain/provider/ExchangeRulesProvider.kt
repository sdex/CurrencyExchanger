package dev.sdex.currencyexchanger.domain.provider

import dev.sdex.currencyexchanger.domain.rules.BaseExchangeFeeRule
import dev.sdex.currencyexchanger.domain.rules.ExchangeFeeRule

class ExchangeRulesProvider {

    val rules = listOf<ExchangeFeeRule>(
        BaseExchangeFeeRule(),
    )
}