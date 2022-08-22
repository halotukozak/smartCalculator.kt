package calculator.Stacks

import calculator.InvalidOperationException
import java.math.BigInteger

class BigIntegerStack : Stack<BigInteger> {
    fun calculate(incoming: String) {
        val first = this.pop()
        val second = this.pop()
        val result = when (incoming) {
            "+" -> first + second
            "-" -> second - first
            "*" -> first * second
            "/" -> second / first
            "^" -> second.pow(first.toInt())
            else -> throw InvalidOperationException()
        }
        this.push(result)
    }

    fun getResult() = storage.first()

    override val storage = mutableListOf<BigInteger>()
}