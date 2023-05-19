package kitakkun.calculator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
) {
    val state by viewModel.state.collectAsState()
    CalculatorView(
        state = state,
        onNumberPadClick = viewModel::insertDigit,
        onOperatorPadClick = viewModel::insertOperator,
        onClearClick = viewModel::clear,
        onEqualClick = viewModel::calculate,
        onDeleteClick = viewModel::deleteChar,
        onDotClick = viewModel::insertDot,
        onBracketClick = viewModel::insertBracket,
    )
}
