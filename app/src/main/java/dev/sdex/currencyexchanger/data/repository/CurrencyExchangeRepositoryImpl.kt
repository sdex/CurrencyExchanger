package dev.sdex.currencyexchanger.data.repository

import dev.sdex.currencyexchanger.data.source.CurrencyExchangeApiService
import dev.sdex.currencyexchanger.data.source.model.Result
import dev.sdex.currencyexchanger.domain.model.ExchangeData
import dev.sdex.currencyexchanger.domain.repository.CurrencyExchangeRepository
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import java.util.concurrent.TimeUnit

private val SYNC_PERIOD = TimeUnit.SECONDS.toMillis(5)

class CurrencyExchangeRepositoryImpl(
    private val apiService: CurrencyExchangeApiService,
) : CurrencyExchangeRepository {

    override fun getExchangeRates(): Flow<Result<ExchangeData>> = flow {
        while (currentCoroutineContext().isActive) {
            emit(Result.Loading())
            try {
                val response = apiService.getExchangeRates()
                emit(
                    Result.Success(
                    ExchangeData(
                        base = response.base!!,
                        date = response.date!!,
                        rates = response.getExchangeRates(),
                    )
                ))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Result.Error(e))
            }
            delay(SYNC_PERIOD)
        }
    }
}