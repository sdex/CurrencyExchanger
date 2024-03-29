package dev.sdex.currencyexchanger.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sdex.currencyexchanger.R
import dev.sdex.currencyexchanger.domain.model.Balance
import dev.sdex.currencyexchanger.domain.model.ExchangeRate
import dev.sdex.currencyexchanger.ui.theme.CurrencyExchangerTheme
import dev.sdex.currencyexchanger.ui.theme.Green
import dev.sdex.currencyexchanger.ui.theme.Red
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinContext
import java.math.BigDecimal

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CurrencyExchangerTheme {
                KoinContext {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .safeDrawingPadding(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val viewModel = koinViewModel<MainViewModel>()
                        val state by viewModel.uiState.collectAsStateWithLifecycle()
                        MainContent(
                            state,
                            viewModel::onEvent,
                            viewModel.actions,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun MainContentPreview() {
    CurrencyExchangerTheme(
        darkTheme = false,
    ) {
        MainContent(
            state = ExchangeCurrencyState(
                balance = listOf(
                    Balance(currency = "EUR", amount = BigDecimal.valueOf(1000.0)),
                    Balance(currency = "USD", amount = BigDecimal.valueOf(50.0)),
                ),
                exchangeRates = listOf(
                    ExchangeRate(currency = "EUR", rate = BigDecimal.valueOf(1.0)),
                    ExchangeRate(currency = "USD", rate = BigDecimal.valueOf(1.1)),
                ),
                message = "Test message",
            ),
            onEvent = {},
            actions = MutableSharedFlow(),
        )
    }
}

@Composable
fun MainContent(
    state: ExchangeCurrencyState,
    onEvent: (UIEvent) -> Unit,
    actions: SharedFlow<Action>,
    ) {
    var sellCurrency by rememberSaveable(state.availableCurrencies) {
        mutableStateOf(state.availableCurrencies.firstOrNull() ?: "")
    }
    var sellAmount by remember { mutableStateOf("") }
    var receiveCurrency by rememberSaveable(state.allCurrencies) {
        mutableStateOf(state.allCurrencies.firstOrNull() ?: "")
    }
    LaunchedEffect(Unit) {
        actions.onEach {
            when (it) {
                is Action.ClearInputField -> {
                    sellAmount = ""
                }
            }
        }.launchIn(this)
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .fillMaxWidth()
                    .height(60.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(R.string.title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Column(
                modifier = Modifier
                    .padding(
                        horizontal = 16.dp,
                        vertical = 16.dp,
                    )
                    .weight(1f, false)
            ) {
                Text(
                    text = stringResource(R.string.my_balances).uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(36.dp),
                ) {
                    items(state.balance) { balance ->
                        Text(
                            text = "${formatDecimal(balance.amount)} ${balance.currency}",
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.currency_exchange).uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Red, CircleShape),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_upward),
                            contentDescription = stringResource(id = R.string.sell),
                            tint = Color.White,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                    Text(
                        text = stringResource(R.string.sell),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    BasicTextField(
                        modifier = Modifier.width(110.dp),
                        value = sellAmount,
                        onValueChange = {
                            sellAmount = if (it.isNotEmpty()) {
                                when (it.toDoubleOrNull()) {
                                    null -> sellAmount
                                    else -> it
                                }
                            } else {
                                ""
                            }
                            onEvent(
                                UIEvent.GetExchangeRate(
                                    sellCurrency,
                                    sellAmount,
                                    receiveCurrency
                                )
                            )
                        },
                        textStyle = TextStyle(
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                        decorationBox = { innerTextField ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                if (sellAmount.isEmpty()) {
                                    Text(
                                        text = "0.00",
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                }
                            }
                            innerTextField()
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                    )
                    CurrencyPicker(
                        state.availableCurrencies,
                        onCurrencySelected = {
                            sellCurrency = it
                            onEvent(
                                UIEvent.GetExchangeRate(
                                    sellCurrency,
                                    sellAmount,
                                    receiveCurrency
                                )
                            )
                        }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider(Modifier.padding(start = 52.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Green, CircleShape),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_downward),
                            contentDescription = stringResource(id = R.string.receive),
                            tint = Color.White,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                    Text(
                        text = stringResource(R.string.receive),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "+${state.receiveAmount}",
                        color = Green,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, true),
                    )
                    CurrencyPicker(
                        state.allCurrencies,
                        onCurrencySelected = {
                            receiveCurrency = it
                            onEvent(
                                UIEvent.GetExchangeRate(
                                    sellCurrency,
                                    sellAmount,
                                    receiveCurrency
                                )
                            )
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                state.message?.let {
                    Text(
                        text = it,
                        color = Red,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
        Button(
            onClick = {
                onEvent(UIEvent.Exchange(sellCurrency, sellAmount, receiveCurrency))
            },
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xff0687c8),
            )
        ) {
            Text(
                text = stringResource(R.string.submit).uppercase(),
                color = Color.White,
            )
        }
    }
    if (state.transactionResult != null) {
        val result = state.transactionResult!!
        AlertDialog(
            onDismissRequest = {
                onEvent(UIEvent.CloseResultDialog)
            },
            title = {
                Text(text = stringResource(R.string.currency_converted))
            },
            text = {
                Text(
                    text = stringResource(
                        R.string.you_have_converted_to_commission_fee,
                        formatDecimal(result.transaction.amount),
                        result.change.sellCurrency,
                        formatDecimal(
                            result.change.buyBalanceChange
                        ),
                        result.change.buyCurrency,
                        formatDecimal(result.transactionFee),
                        result.change.sellCurrency
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onEvent(UIEvent.CloseResultDialog)
                    }) {
                    Text(stringResource(R.string.done))
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyPicker(
    currencies: List<String>,
    onCurrencySelected: (String) -> Unit,
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    var selectedCurrency by remember(key1 = currencies) {
        mutableStateOf(currencies.firstOrNull() ?: "")
    }
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { newValue ->
            isExpanded = newValue
        },
        modifier = Modifier.width(110.dp),
    ) {
        OutlinedTextField(
            value = selectedCurrency,
            onValueChange = { },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
            modifier = Modifier.menuAnchor(),
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = {
                isExpanded = false
            }
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = currency,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    onClick = {
                        selectedCurrency = currency
                        isExpanded = false
                        onCurrencySelected(currency)
                    }
                )
            }
        }
    }
}