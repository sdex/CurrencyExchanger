package dev.sdex.currencyexchanger.domain.model

import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicLong

data class ExchangeTransaction(
    val id: Long = ID.incrementAndGet(),
    val sellCurrency: String,
    val buyCurrency: String,
    val amount: BigDecimal,
) {

    companion object {
        private val ID: AtomicLong = AtomicLong(0)
    }
}
