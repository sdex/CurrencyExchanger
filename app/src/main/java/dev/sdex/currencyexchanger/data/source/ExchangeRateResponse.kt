package dev.sdex.currencyexchanger.data.source

import com.google.gson.annotations.SerializedName
import dev.sdex.currencyexchanger.domain.model.ExchangeRate

data class ExchangeRateResponse(
    @SerializedName("base")
    var base: String? = null,
    @SerializedName("date")
    var date: String? = null,
    @SerializedName("rates")
    var rates: Map<String, Double>? = emptyMap(),
) {

    fun getExchangeRates(): List<ExchangeRate> {
        return rates?.map {
            ExchangeRate(
                currency = it.key,
                rate = it.value,
            )
        } ?: emptyList()
    }

}