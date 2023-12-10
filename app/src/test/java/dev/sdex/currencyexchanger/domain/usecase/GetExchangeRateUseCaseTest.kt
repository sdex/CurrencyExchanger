package dev.sdex.currencyexchanger.domain.usecase

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class GetExchangeRateUseCaseTest {

    private lateinit var useCase: GetExchangeRateUseCase
    private lateinit var repository: FakeCurrencyExchangeRepository

    @BeforeEach
    fun setUp() {
        repository = FakeCurrencyExchangeRepository()
        useCase = GetExchangeRateUseCase()
    }

    @Test
    fun `Get exchange rate for EUR to USD` (): Unit = runBlocking{
        val rate = useCase(
            exchangeRates = repository.getExchangeRates().first().data!!.rates,
            sellCurrency = "EUR",
            buyCurrency = "USD",
        )
        assertThat(rate).isEqualTo(BigDecimal("1.1"))
    }

    @Test
    fun `Get exchange rate for USD to UAH` (): Unit = runBlocking{
        val rate = useCase(
            exchangeRates = repository.getExchangeRates().first().data!!.rates,
            sellCurrency = "USD",
            buyCurrency = "UAH",
        )
        assertThat(rate).isEqualTo(BigDecimal("36"))
    }
}