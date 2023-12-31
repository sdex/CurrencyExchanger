package dev.sdex.currencyexchanger.data.source

import dev.sdex.currencyexchanger.data.source.model.ExchangeRateResponse
import retrofit2.http.GET

interface CurrencyExchangeApiService {

    @GET("currency-exchange-rates")
    suspend fun getExchangeRates(): ExchangeRateResponse

    companion object {
        const val BASE_URL: String = "https://developers.paysera.com/tasks/api/"
    }
}