package dev.sdex.currencyexchanger.domain.repository

import dev.sdex.currencyexchanger.data.source.model.Result
import dev.sdex.currencyexchanger.domain.model.ExchangeData
import kotlinx.coroutines.flow.Flow

interface CurrencyExchangeRepository {

    fun getExchangeRates(): Flow<Result<ExchangeData>>

}