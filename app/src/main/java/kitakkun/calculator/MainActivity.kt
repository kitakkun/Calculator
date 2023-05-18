package kitakkun.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kitakkun.calculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var editingBrackets: Int = 0

    private val calculator: StringMathEvaluator = StringMathEvaluator()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.inputFormula = ""
        binding.calcPrediction = ""
        binding.activity = this

        val view = binding.root
        setContentView(view)
    }

    fun onDigitBtnClicked(view: View)
    {
        binding.inputFormula += (view as Button).text
        onMathStatementChanged()
    }

    fun onClearBtnClicked()
    {
        binding.inputFormula = ""
        binding.calcPrediction = ""
        editingBrackets = 0
    }

    fun onBackSpaceBtnClicked()
    {
        val inputFormula = binding.inputFormula
        if (inputFormula!!.isNotEmpty()) {
            if (inputFormula[inputFormula.length - 1] == '(') {
                editingBrackets--
            }
            binding.inputFormula = inputFormula.substring(0, inputFormula.length - 1)
        }
        onMathStatementChanged()
    }

    fun onBracketBtnClicked()
    {
        if (editingBrackets > 0 && (lastIs(CharType.Number) || lastIs(CharType.BracketEnd)) && !lastIs(CharType.Dot))
        {
            editingBrackets--
            binding.inputFormula += ")"
        }
        else
        {
            binding.inputFormula += "("
            editingBrackets++
        }
        onMathStatementChanged()
    }

    fun onEqualBtnClicked()
    {
        val result = calculator.calc(binding.inputFormula!!)
        result?.let {
            val str = Regex(".0+\$").replace(it.toString(), "")
            binding.inputFormula = str
        }
    }

    fun onDotBtnClicked()
    {
        var continuingNumber = true
        var dotAlreadyExists = false
        val str = binding.inputFormula!!
        for (i in str.length - 1 downTo 0) {
            if (!continuingNumber) {
                break
            }
            if (str[i].toString().matches(Regex("[0-9]")))
            {
                continuingNumber = true
            }
            else if (str[i] == '.')
            {
                dotAlreadyExists = true
                continuingNumber = false
            }
            else
            {
                continuingNumber = false
            }
        }

        if (lastIs(CharType.Number) && !lastIs(CharType.Dot) && !dotAlreadyExists)
        {
            // Add Decimal Point
            binding.inputFormula += "."
            onMathStatementChanged()
        }
    }

    fun onOperatorBtnClicked(view: View)
    {
        if (lastIs(CharType.Number, CharType.BracketEnd) && !lastIs(CharType.Operator))
        {
            binding.inputFormula += (view as Button).text
            onMathStatementChanged()
        }
    }

    private fun onMathStatementChanged()
    {
        val result = calculator.calc(binding.inputFormula!!)

        if (result != null)
        {
            val str = Regex(".0+\$").replace(result.toString(), "")
            binding.calcPrediction = str
        }
        else
        {
            binding.calcPrediction = ""
        }

    }

    enum class CharType
    {
        Number, Dot, BracketStart, BracketEnd, Operator, Empty
    }

    private fun lastIs(type: CharType) : Boolean
    {
        return getLastCharType(binding.inputFormula!!) == type
    }

    // OR演算をします
    private fun lastIs(vararg types: CharType) : Boolean
    {
        var result = false
        for (type in types)
        {
            result = result || lastIs(type)
        }
        return result
    }

    private fun getLastCharType(mathString: String) : CharType
    {
        if (mathString.isEmpty())
        {
            return CharType.Empty
        }
        return when (mathString[mathString.length - 1])
        {
            '(' -> CharType.BracketStart
            ')' -> CharType.BracketEnd
            '.' -> CharType.Dot
            '+', '-', '/', '*', '%' -> CharType.Operator
            else -> CharType.Number
        }

    }

}