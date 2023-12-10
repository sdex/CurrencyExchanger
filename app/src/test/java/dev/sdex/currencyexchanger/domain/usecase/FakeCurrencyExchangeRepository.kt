package dev.sdex.currencyexchanger.domain.usecase

import dev.sdex.currencyexchanger.data.source.model.Result
import dev.sdex.currencyexchanger.domain.model.ExchangeData
import dev.sdex.currencyexchanger.domain.model.ExchangeRate
import dev.sdex.currencyexchanger.domain.repository.CurrencyExchangeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.math.BigDecimal

class FakeCurrencyExchangeRepository(): CurrencyExchangeRepository {

    override fun getExchangeRates(): Flow<Result<ExchangeData>> {
        return flow {
            emit(Result.Success(
                ExchangeData(
                    base = "EUR",
                    date = "2023-12-10",
                    rates = listOf(
                        ExchangeRate(
                            currency = "EUR",
                            rate = BigDecimal("1.0"),
                        ),
                        ExchangeRate(
                            currency = "USD",
                            rate = BigDecimal("1.1"),
                        ),
                        ExchangeRate(
                            currency = "UAH",
                            rate = BigDecimal("40"),
                        ),
                    )
                )
            ))
        }
    }
}