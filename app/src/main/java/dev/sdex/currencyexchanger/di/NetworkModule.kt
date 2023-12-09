package dev.sdex.currencyexchanger.di

import dev.sdex.currencyexchanger.data.source.CurrencyExchangeApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {

    single<OkHttpClient> {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(CurrencyExchangeApiService.BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .client(get())
            .build()
            .create(CurrencyExchangeApiService::class.java)
    }
}