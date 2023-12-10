package dev.sdex.currencyexchanger.domain.model

import java.math.BigDecimal

data class BalanceChange(
    val sellCurrency: String,
    val sellBalanceChange: BigDecimal,
    val buyCurrency: String,
    val buyBalanceChange: BigDecimal,
)

data class ExchangeTransactionResult(
    val transaction: ExchangeTransaction,
    val isProcessed: Boolean,
    val transactionFee: BigDecimal,
    val change: BalanceChange,
)