package me.riafy.finlog.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import me.riafy.finlog.data.models.CurrencyInfo
import me.riafy.finlog.ui.theme.MoneyDisplayStyle

/**
 * The large amount field that leads Add Expense. Accepts only digits and, once,
 * a decimal point with up to as many places as the currency allows - so the raw
 * text is always a value [me.riafy.finlog.data.models.Money.fromInput] can parse.
 */
@Composable
fun MoneyInput(
    text: String,
    onTextChange: (String) -> Unit,
    currencyCode: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MoneyDisplayStyle
) {
    val decimalDigits = CurrencyInfo.decimalDigits(currencyCode)

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = CurrencyInfo.symbolFor(currencyCode),
            style = style,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(4.dp))
        BasicTextField(
            value = text,
            onValueChange = { candidate -> onTextChange(sanitize(candidate, decimalDigits)) },
            textStyle = style.copy(color = MaterialTheme.colorScheme.onSurface),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            singleLine = true
        )
    }
}

private fun sanitize(input: String, decimalDigits: Int): String {
    if (input.isEmpty()) return input
    val allowedChars = input.filterIndexed { index, char ->
        char.isDigit() || (char == '.' && decimalDigits > 0 && input.indexOf('.') == index)
    }
    val parts = allowedChars.split(".")
    if (parts.size <= 1) return allowedChars
    val fraction = parts[1].take(decimalDigits)
    return "${parts[0]}.$fraction"
}
