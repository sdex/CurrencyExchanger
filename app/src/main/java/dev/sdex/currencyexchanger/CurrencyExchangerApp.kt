package dev.sdex.currencyexchanger

import android.app.Application
import dev.sdex.currencyexchanger.di.appModule
import dev.sdex.currencyexchanger.di.exchangeRateRepositoryModule
import dev.sdex.currencyexchanger.di.exchangeRateUseCaseModule
import dev.sdex.currencyexchanger.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class CurrencyExchangerApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@CurrencyExchangerApp)
            modules(
                appModule,
                networkModule,
                exchangeRateRepositoryModule,
                exchangeRateUseCaseModule,
            )
        }
    }
}