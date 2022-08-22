package calculator.stacks

import calculator.exceptions.InvalidOperationException
import java.math.BigInteger

class BigIntegerStack : Stack<BigInteger> {
    fun calculate(incoming: String) {
        val (first, second) = List(2) { this.pop() }
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