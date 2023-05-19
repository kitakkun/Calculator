package kitakkun.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly

@Composable
fun CalculatorView(
    state: CalculatorState,
    onNumberPadClick: (Int) -> Unit,
    onOperatorPadClick: (String) -> Unit,
    onClearClick: () -> Unit,
    onEqualClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDotClick: () -> Unit,
    onBracketClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = state.expression,
            fontSize = 48.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.End,
        )
        Text(
            text = state.result,
            fontSize = 24.sp,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                listOf(
                    "AC", "()", "%", "/",
                    "7", "8", "9", "*",
                    "4", "5", "6", "-",
                    "1", "2", "3", "+",
                    "0", ".", "del", "=",
                )
            ) {
                CalculatorButton(
                    text = it,
                    onClick = {
                        when {
                            it.isDigitsOnly() -> onNumberPadClick(it.toInt())
                            it.isOperator() -> onOperatorPadClick(it)
                            it == "del" -> onDeleteClick()
                            it == "=" -> onEqualClick()
                            it == "." -> onDotClick()
                            it == "()" -> onBracketClick()
                            it == "AC" -> onClearClick()
                        }
                    }
                )
            }
        }
    }
}

private fun String.isOperator(): Boolean {
    return this in listOf("+", "-", "*", "/", "%")
}

@Composable
private fun CalculatorButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(1f),
        shape = RoundedCornerShape(15),
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun CalculatorViewPreview() {
    CalculatorView(
        state = CalculatorState(
            expression = "1+1",
            result = "2",
        ),
        onNumberPadClick = {},
        onOperatorPadClick = {},
        onClearClick = {},
        onEqualClick = {},
        onDeleteClick = {},
        onDotClick = {},
        onBracketClick = {},
    )
}
