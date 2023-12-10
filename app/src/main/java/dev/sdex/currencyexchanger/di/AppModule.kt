package dev.sdex.currencyexchanger.di

import dev.sdex.currencyexchanger.data.repository.CurrencyExchangeRepositoryImpl
import dev.sdex.currencyexchanger.domain.provider.ExchangeRulesProvider
import dev.sdex.currencyexchanger.domain.repository.CurrencyExchangeRepository
import dev.sdex.currencyexchanger.domain.usecase.ExchangeUseCase
import dev.sdex.currencyexchanger.domain.usecase.GetExchangeRateUseCase
import dev.sdex.currencyexchanger.domain.usecase.GetExchangeRatesUseCase
import dev.sdex.currencyexchanger.ui.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel {
        MainViewModel(
            getExchangeRatesUseCase = get(),
            exchangeUseCase = get(),
            getExchangeRateUseCase = get(),
        )
    }
}

val exchangeRateRepositoryModule = module {
    single<CurrencyExchangeRepository> { CurrencyExchangeRepositoryImpl(apiService = get()) }
}

val exchangeUseCaseModule = module {
    single { GetExchangeRatesUseCase(repository = get()) }
    single {
        ExchangeUseCase(
            ExchangeRulesProvider(),
            getExchangeRateUseCase = get(),
        )
    }
    single { GetExchangeRateUseCase() }
}