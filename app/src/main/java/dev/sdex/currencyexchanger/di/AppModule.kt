package dev.sdex.currencyexchanger.di

import dev.sdex.currencyexchanger.data.repository.CurrencyExchangeRepository
import dev.sdex.currencyexchanger.data.repository.CurrencyExchangeRepositoryImpl
import dev.sdex.currencyexchanger.domain.usecase.GetExchangeRate
import dev.sdex.currencyexchanger.ui.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { MainViewModel(getExchangeRate = get()) }
}

val exchangeRateRepositoryModule = module {
    factory<CurrencyExchangeRepository> { CurrencyExchangeRepositoryImpl(apiService = get()) }
}

val exchangeRateUseCaseModule = module {
    factory { GetExchangeRate(repository = get()) }
}