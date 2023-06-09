package kitakkun.calculator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StringMathEvaluatorTest {
    private val evaluator = StringMathEvaluator()

    @Test
    fun testEvaluation() {
        // test various cases
        assertEquals(-77.0, evaluator.calc("1+(1-(4*5))+(-10)*(59)/(100-80)*2"))
        assertEquals(0.0, evaluator.calc("1+1-2"))
        assertEquals(-12.0, evaluator.calc("-1*10-2"))
        assertEquals(2.5, evaluator.calc("(10/2)%50"))
        assertEquals(40.0, evaluator.calc("(-10-50+100)"))
        assertEquals(160.0, evaluator.calc("(80)2"))
        assertEquals(50.0, evaluator.calc("(100)%(50)"))
        assertEquals(50.0, evaluator.calc("(((100)%50))"))
    }

    @Test
    fun testBracketValidation() {
        assertTrue(evaluator.isBracketsValid("1+(1-(4*5))+(-10)*(59)/(100-80)*2"))
        assertTrue(evaluator.isBracketsValid("(1+2)*2"))
        assertFalse(evaluator.isBracketsValid("(1+(2)*2"))
        assertFalse(evaluator.isBracketsValid("1+(1-(4*5))+(-10)*(59)/(100-80)*2)"))
    }

    @Test
    fun testSplitToTerms() {
        assertEquals(listOf("1", "+", "2", "-", "3"), evaluator.splitToTerms("1+2-3"))
        assertEquals(listOf("((20+20)+(30+30))"), evaluator.splitToTerms("((20+20)+(30+30))"))
        assertEquals(
            listOf(
                "1",
                "+",
                "(1-(4*5))",
                "+",
                "(-10)",
                "*",
                "(59)",
                "/",
                "(100-80)",
                "*",
                "2"
            ), evaluator.splitToTerms("1+(1-(4*5))+(-10)*(59)/(100-80)*2")
        )
    }

    @Test
    fun testMultiplierCompletion() {
        assertEquals("(80)*2", evaluator.completeMultiply("(80)2"))
        assertEquals("((80)*2*(50)/3*(20))", evaluator.completeMultiply("((80)2(50)/3(20))"))
    }
}
