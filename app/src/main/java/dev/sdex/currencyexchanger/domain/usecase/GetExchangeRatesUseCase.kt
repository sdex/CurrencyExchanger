package dev.sdex.currencyexchanger.domain.usecase

import dev.sdex.currencyexchanger.data.source.model.Result
import dev.sdex.currencyexchanger.domain.model.ExchangeData
import dev.sdex.currencyexchanger.domain.repository.CurrencyExchangeRepository
import kotlinx.coroutines.flow.Flow

class GetExchangeRatesUseCase(
    private val repository: CurrencyExchangeRepository,
) {

    operator fun invoke(): Flow<Result<ExchangeData>> {
        return repository.getExchangeRates()
    }
}