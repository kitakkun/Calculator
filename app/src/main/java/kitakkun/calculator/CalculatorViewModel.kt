package kitakkun.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalculatorViewModel : ViewModel() {

    private val mutableState = MutableStateFlow(CalculatorState())
    val state = mutableState.asStateFlow()

    private val evaluator = StringMathEvaluator()

    init {
        viewModelScope.launch {
            mutableState
                .distinctUntilChanged { old, new ->
                    old.expression == new.expression
                }
                .collectLatest {
                    mutableState.update {
                        it.copy(
                            result = evaluator.calc(it.expression)?.toString()
                                ?.removeUnnecessaryFloatPoint() ?: ""
                        )
                    }
                }
        }
    }

    fun insertDigit(number: Int) {
        mutableState.update {
            it.copy(
                expression = it.expression + number.toString(),
            )
        }
    }

    fun deleteChar() {
        mutableState.update {
            it.copy(
                expression = it.expression.dropLast(1),
            )
        }
    }

    fun clear() {
        mutableState.update {
            it.copy(
                expression = "",
            )
        }
    }

    fun insertOperator(operator: String) {
        if (lastIs(CharType.Number, CharType.BracketEnd) && !lastIs(CharType.Operator)) {
            mutableState.update {
                it.copy(
                    expression = it.expression + operator,
                )
            }
        }
    }

    fun insertDot() {
        var continuingNumber = true
        var dotAlreadyExists = false
        val str = state.value.expression
        for (i in str.length - 1 downTo 0) {
            if (!continuingNumber) {
                break
            }
            if (str[i].toString().matches(Regex("[0-9]"))) {
                continuingNumber = true
            } else if (str[i] == '.') {
                dotAlreadyExists = true
                continuingNumber = false
            } else {
                continuingNumber = false
            }
        }

        if (lastIs(CharType.Number) && !lastIs(CharType.Dot) && !dotAlreadyExists) {
            // Add Decimal Point
            mutableState.update {
                it.copy(
                    expression = it.expression + ".",
                )
            }
        }
    }

    fun insertBracket() {
        val oldState = state.value
        val editingBrackets =
            oldState.expression.count { it == '(' } - oldState.expression.count { it == ')' }
        if (editingBrackets > 0 && (lastIs(CharType.Number) || lastIs(CharType.BracketEnd)) && !lastIs(
                CharType.Dot
            )
        ) {
            mutableState.update {
                it.copy(
                    expression = it.expression + ")",
                )
            }
        } else {
            mutableState.update {
                it.copy(
                    expression = it.expression + "(",
                )
            }
        }
    }

    fun calculate() {
        val calcResult = evaluator.calc(state.value.expression) ?: return
        mutableState.update {
            it.copy(
                expression = calcResult.toString().removeUnnecessaryFloatPoint(),
            )
        }
    }

    enum class CharType {
        Number, Dot, BracketStart, BracketEnd, Operator, Empty
    }

    private fun lastIs(type: CharType): Boolean {
        return getLastCharType(state.value.expression) == type
    }

    // OR演算をします
    private fun lastIs(vararg types: CharType): Boolean {
        var result = false
        for (type in types) {
            result = result || lastIs(type)
        }
        return result
    }

    private fun getLastCharType(mathString: String): CharType {
        if (mathString.isEmpty()) {
            return CharType.Empty
        }
        return when (mathString[mathString.length - 1]) {
            '(' -> CharType.BracketStart
            ')' -> CharType.BracketEnd
            '.' -> CharType.Dot
            '+', '-', '/', '*', '%' -> CharType.Operator
            else -> CharType.Number
        }
    }

    private fun String.removeUnnecessaryFloatPoint(): String {
        return Regex(".0+\$").replace(this, "")
    }
}
