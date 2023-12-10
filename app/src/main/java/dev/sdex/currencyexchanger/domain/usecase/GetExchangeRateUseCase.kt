package dev.sdex.currencyexchanger.domain.usecase

import dev.sdex.currencyexchanger.domain.model.ExchangeRate
import java.math.BigDecimal

class GetExchangeRateUseCase {

    operator fun invoke(
        exchangeRates: List<ExchangeRate>,
        sellCurrency: String,
        buyCurrency: String,
    ): BigDecimal {
        val sellRate = exchangeRates.first { it.currency == sellCurrency }.rate
        val buyRate = exchangeRates.first { it.currency == buyCurrency }.rate
        return buyRate / sellRate
    }
}