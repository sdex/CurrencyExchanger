package dev.sdex.currencyexchanger.domain.rules

import dev.sdex.currencyexchanger.domain.model.ExchangeTransaction
import java.math.BigDecimal

/**
 * First 5 transactions are fee-free,
 * afterwards the fee is 0.7%
 */
class BaseExchangeFeeRule : ExchangeFeeRule {

    private val fee = BigDecimal.valueOf(0.007)

    override fun process(
        transaction: ExchangeTransaction
    ): ExchangeFeeRuleResult {
        val fee = if (transaction.id > 5) {
            transaction.amount * fee
        } else {
            null
        }
        return ExchangeFeeRuleResult(fee)
    }
}