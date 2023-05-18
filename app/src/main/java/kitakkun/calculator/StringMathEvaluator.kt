package kitakkun.calculator

import androidx.annotation.VisibleForTesting
import java.util.Stack

class StringMathEvaluator {
    private fun Char.isOperator(): Boolean {
        return this in listOf('+', '-', '*', '/', '%')
    }

    fun calc(mathExpression: String): Double? {
        // 括弧の数が一致しない場合は不正
        if (!isBracketsValid(mathExpression)) return null

        // 数値評価が可能な状態であれば数値を返す
        mathExpression.toDoubleOrNull()?.let {
            return it
        }

        // 一番外側の括弧は外す
        val expr = expandBrackets(mathExpression)

        // 分割処理
        val terms: List<String> = splitToTerms(expr)

        // オペランド不足の場合不正
        if (terms.size % 2 == 0) {
            return null
        }

        // 補足:
        // 割り算を先に行うのは，割る数が大きくなり誤った計算をしてしまうのを防ぐためである．
        // 例) 5 / 10 * 2を掛け算を優先的にやると 5 / 20 = 1/4 という誤った数値になる
        // 引き算を先に行うのも同様
        val calcResult = terms
            .completeMultiplyOperators() // 省略されている*を補完する
            .calculateWithOperator("%") // パーセントを計算する
            .calculateWithOperator("/") // 割り算を計算する
            .calculateWithOperator("*") // 掛け算を計算する
            .calculateWithOperator("-") // 引き算を計算する
            .calculateWithOperator("+") // 足し算を計算する

        return calcResult[0].toDoubleOrNull()
    }

    @VisibleForTesting
    fun isBracketsValid(mathString: String): Boolean {
        val stack = Stack<Char>()
        for (c in mathString) {
            when (c) {
                '(' -> stack.push(c)
                ')' -> {
                    if (stack.isEmpty()) return false
                    stack.pop()
                }
            }
        }
        if (stack.isNotEmpty()) return false
        return true
    }

    // 指定した演算子の計算処理を行う
    private fun List<String>.calculateWithOperator(operator: String): List<String> {
        val result = this.toMutableList()
        while (result.contains(operator)) {
            val operatorIndex = result.indexOf(operator)
            val operand1 = calc(result[operatorIndex - 1]) ?: return emptyList()
            val operand2 = calc(result[operatorIndex + 1]) ?: return emptyList()

            val calcResult = when (operator) {
                "+" -> operand1 + operand2
                "-" -> operand1 - operand2
                "*" -> operand1 * operand2
                "/" -> operand1 / operand2
                "%" -> operand1 * (operand2 / 100.0)
                else -> null
            }
            repeat(3) {
                result.removeAt(operatorIndex - 1)
            }
            result.add(operatorIndex - 1, calcResult.toString())
        }
        return result
    }

    // 省略された掛け算を補完する
    private fun List<String>.completeMultiplyOperators(): List<String> {
        val numBeforeBracketPattern = Regex("([0-9]|\\.)+\\(.*")
        val numAfterBracketPattern = Regex(".*\\)([0-9]|\\.)+")
        return this.map {
            it.replace(")(", ")*(")
                .replace(numBeforeBracketPattern) {
                    val str = it.value
                    val opl1 = str.substring(0, str.indexOf("("))
                    str.substring(str.indexOf("(")).let {
                        "$opl1*$it"
                    }
                }
                .replace(numAfterBracketPattern) {
                    val str = it.value
                    val opl2 = str.substring(str.indexOf(")"))
                    str.substring(0, str.indexOf(")")).let {
                        "$it*$opl2"
                    }
                }
        }
    }

    // 外側の括弧を展開する
    private fun expandBrackets(mathString: String): String =
        when (mathString.startsWith("(") && mathString.endsWith(")")) {
            true -> mathString.substring(1, mathString.length - 1)
            false -> mathString
        }

    // 項と演算子で分割を行います
    @VisibleForTesting
    fun splitToTerms(mathString: String): List<String> {
        val result = mutableListOf<String>()
        val termStack = Stack<Char>()
        mathString.forEachIndexed { index, char ->
            if (char.isOperator() && isBracketsValid(termStack.joinToString(""))) {
                // 先頭でマイナス符号の時は最初に0を挿入してあげる
                // 各演算で演算子の左右に1つずつ項が存在しないと計算できない仕様のため
                if (index == 0 && char == '-') {
                    result.add("0")
                }
                result.add(termStack.joinToString(""))
                result.add(char.toString())
                termStack.clear()
            } else {
                termStack.push(char)
            }
        }
        result.add(termStack.joinToString(""))
        return result.filter { it.isNotEmpty() }
    }
}
