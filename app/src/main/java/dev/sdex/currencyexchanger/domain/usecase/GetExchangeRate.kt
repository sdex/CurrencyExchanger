package dev.sdex.currencyexchanger.domain.usecase

import dev.sdex.currencyexchanger.data.repository.CurrencyExchangeRepository
import dev.sdex.currencyexchanger.data.source.Result
import dev.sdex.currencyexchanger.domain.model.ExchangeRate
import kotlinx.coroutines.flow.Flow

class GetExchangeRate(
    private val repository: CurrencyExchangeRepository,
) {

    operator fun invoke(): Flow<Result<List<ExchangeRate>>> {
        return repository.getExchangeRates()
    }
}