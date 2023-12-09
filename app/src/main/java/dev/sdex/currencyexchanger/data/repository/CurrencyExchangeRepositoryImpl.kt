package dev.sdex.currencyexchanger.data.repository

import dev.sdex.currencyexchanger.data.source.CurrencyExchangeApiService
import dev.sdex.currencyexchanger.data.source.Result
import dev.sdex.currencyexchanger.domain.model.ExchangeRate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CurrencyExchangeRepositoryImpl(
    private val apiService: CurrencyExchangeApiService,
) : CurrencyExchangeRepository {

    override fun getExchangeRates(): Flow<Result<List<ExchangeRate>>> = flow {
        emit(Result.Loading())
        try {
            val response = apiService.getExchangeRates()
            emit(Result.Success(response.getExchangeRates()))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e))
        }
    }
}