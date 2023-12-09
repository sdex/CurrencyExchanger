package dev.sdex.currencyexchanger.data.repository

import dev.sdex.currencyexchanger.data.source.Result
import dev.sdex.currencyexchanger.domain.model.ExchangeRate
import kotlinx.coroutines.flow.Flow

interface CurrencyExchangeRepository {

    fun getExchangeRates(): Flow<Result<List<ExchangeRate>>>

}