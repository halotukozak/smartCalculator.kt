package calculator

import calculator.Operator.Companion.leftBracket
import java.math.BigInteger


fun main() {
    Calculator.run()
}

object Calculator {
    private val variables = VariableMap(
//        "A" to 1, "B" to 2, "C" to 3, "D" to 4
    )

    fun run() {
        var exit = false
        while (!exit) {
            try {
                val input = readln()
//                val input = "A * (B + C) / D"
//                val input = "33 + 20 + 11 + 49 - 32 - 9 + 1 - 80 + 4"
                when {
                    input.isEmpty() -> continue
                    input.matches("^/[A-z]*".toRegex()) -> exit = doCommand(input)
                    input.contains("=") -> setVariable(input)
                    input.isValidVariableName() -> {
                        val value = variables.getVariable(input)
                        println(value)
                    }

                    else -> {
                        val stack = calculate(input)
                        println(stack)
                    }
                }
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    private fun doCommand(input: String): Boolean {
        return when (input) {
            "/exit" -> {
                println("Bye!")
                true
            }

            "/help" -> {
                println("The program calculates the sum and subtractions of numbers")
                false
            }

            else -> {
                throw UnknownCommandException()
            }
        }
    }


    private fun setVariable(input: String) {
        if (input.isInvalidAssignment()) throw InvalidAssignmentException()
        val (varName, varValue) = input.split("=").map { it.trim() }
        if (varName.isInvalidVariableName()) throw InvalidIdentifierException()
        variables[varName] = varValue.toBigIntegerOrNull() ?: variables.getVariable(varValue)
    }


    private fun calculate(input: String): BigInteger {
        val inputList = prepareInput(input)
        val postfixInput = convertToPostfix(inputList)
        val stack = BigIntegerStack()
        postfixInput.forEach { incoming ->
            when {
                incoming.isNumeric() -> stack.push(incoming.toBigInteger())
                incoming.isValidOperator() -> stack.calculate(incoming)
                incoming.isValidVariableName() -> stack.push(variables.getVariable(incoming))
                else -> throw InvalidExpressionException()
            }
        }
        return stack.getResult()
    }

    private fun prepareInput(input: String): InputStack {
        if (input.count { it == '(' } != input.count { it == ')' } || input.contains("[*/]{2,}".toRegex())) throw InvalidAssignmentException()
        val output = InputStack()
        val smoothInput = input.removeRedundantOperators()
        smoothInput.forEach { char ->
            when {
                char.isValidOperator() -> {
                    output.push(char)
                }

                char.isDigit() -> {
                    if (output.isNotEmpty() && output.last()!!.isNumeric()) output.addToLast(char)
                    else output.push(char)
                }

                char.isLetter() -> {
                    if (output.isNotEmpty() && output.last()!!.isValidVariableName()) output.addToLast(char)
                    else output.push(char)
                }

                else -> {
                    throw InvalidIdentifierException()
                }
            }
        }
        return output
    }


    private fun convertToPostfix(inputList: InputStack): InputStack {

        fun addTopToOutput(stack: OperatorStack, output: InputStack) {
            val top = stack.pop().toString()
            output.push(top)
        }

        val stack = OperatorStack()
        val output = InputStack()
        inputList.forEach { incoming ->
            if (incoming.isValidVariableName() || incoming.isNumeric()) {
                output.push(incoming)
                return@forEach
            }
            if (incoming.isValidOperator()) {
                val operator = Operator(incoming)
                when {
                    operator == Operator.rightBracket() -> {
                        while (stack.last() != leftBracket()) {
                            addTopToOutput(stack, output)
                        }
                        stack.pop()
                    }

                    stack.isEmpty() || stack.last() == leftBracket() || operator == leftBracket() || stack.hasHigherPrecedenceThanTop(
                        operator
                    ) -> {
                        stack.push(
                            operator
                        )
                    }

                    stack.hasLowerOrEqualPrecedenceThanTop(operator) -> {
                        while (stack.hasLowerOrEqualPrecedenceThanTop(operator) && stack.last() != leftBracket()) {
                            addTopToOutput(stack, output)
                        }
                        stack.push(operator)
                    }

                }
            }
        }

        while (stack.isNotEmpty()) {
            addTopToOutput(stack, output)
        }

        return output

    }


}

//private fun BigInteger.pow(index: Int): BigInteger {
//    var result = this
//    for(i in 1 until index) {
//        result *= result
//    }
//    return result
//}

private fun String.isInvalidAssignment(): Boolean = !this.replace(" ", "").matches("[A-z]+=-?[A-z0-9]+".toRegex())
private fun String.isValidVariableName(): Boolean = this.matches("[A-Za-z]+".toRegex())
private fun String.isValidOperator(): Boolean = this.matches("[+\\-*/()^]".toRegex())
private fun Char.isValidOperator(): Boolean = this in listOf('+', '-', '*', '/', '(', ')', '^')
private fun String.isInvalidVariableName(): Boolean = !this.isValidVariableName()
private fun String.isNumeric(): Boolean {
    val regex = "-?[0-9]+(\\.[0-9]+)?".toRegex()
    return this.matches(regex)
}

private fun String.removeRedundantOperators(): String {
    var temp = this.replace(" ", "")
    val replacePairs = listOf("--" to "+", "++" to "+", "+-" to "-")
    for ((old, new) in replacePairs) {
        while (temp.contains(old)) temp = temp.replace(old, new)
    }
    return temp
}


class UnknownVariableException : Exception("Unknown variable")
class UnknownCommandException : Exception("Unknown command")
class InvalidExpressionException : Exception("Invalid expression")
class InvalidIdentifierException : Exception("Invalid identifier")
class InvalidOperationException : Exception("Invalid operation")
class InvalidAssignmentException : Exception("Invalid assigment")
class EmptyStackException : Exception("Empty stack")


class VariableMap(vararg args: Pair<String, BigInteger>) : MutableMap<String, BigInteger> by mutableMapOf(*args) {
    fun getVariable(name: String): BigInteger {
        if (name.isInvalidVariableName()) throw InvalidAssignmentException()
        return this[name] ?: throw UnknownVariableException()
    }
}

class InputStack : Stack<String> {

    override val storage = mutableListOf<String>()
    fun push(e: Char) {
        push(e.toString())
    }

    fun push(e: Operator) {
        push(e.toString())
    }

    fun addToLast(e: Char) {
        storage[storage.size - 1] += e.toString()
    }

}

interface Stack<T> {
    val storage: MutableList<T>
    fun asString(): String {
        return buildString {
            appendLine("----top----")
            storage.asReversed().forEach {
                appendLine(it)
            }
            appendLine("-----------")
        }
    }

    fun push(element: T) = storage.add(element)
    fun pop(): T {
        if (storage.size == 0) throw EmptyStackException()
        return storage.removeAt(storage.size - 1)
    }

    fun isEmpty(): Boolean = storage.isEmpty()
    fun isNotEmpty(): Boolean = !isEmpty()
    fun last(): T? = storage.lastOrNull()
    fun forEach(action: (T) -> Unit) {
        for (element in storage) action(element)
    }
}


class OperatorStack : Stack<Operator> {
    override val storage = mutableListOf<Operator>()
    override fun toString(): String = asString()

    fun hasEqualPrecedenceOnTop(operator: Operator):Boolean{
        val lastOperator = this.last()
        return operator.priority == lastOperator.priority
    }

    fun hasHigherPrecedenceThanTop(operator: Operator): Boolean {
        val lastOperator = this.last()
        return operator.priority > lastOperator.priority
    }

    fun hasLowerPrecedenceThanTop(operator: Operator): Boolean {
        val lastOperator = this.last()
        return operator.priority < lastOperator.priority
    }

    fun hasHigherOrEqualPrecedenceThanTop(operator: Operator): Boolean {
        return hasHigherPrecedenceThanTop(operator) || hasEqualPrecedenceOnTop(operator)
    }

    fun hasLowerOrEqualPrecedenceThanTop(operator: Operator): Boolean {
        return hasLowerPrecedenceThanTop(operator) || hasEqualPrecedenceOnTop(operator)
    }

    override fun last(): Operator {
        return super.last() ?: Operator()
    }

}

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

class Operator(operator: String = "") {
    private val stringOperator = operator
    override fun toString(): String = stringOperator

    val priority: Int = when (operator) {
        "" -> zeroPriority
        "+" -> additionPriority
        "-" -> subtractionPriority
        "*" -> multiplicationPriority
        "/" -> divisionPriority
        "^" -> powerPriority
        "(", ")" -> bracketPriority
        else -> throw InvalidOperationException()
    }

    override fun equals(other: Any?): Boolean = (other is Operator) && other.stringOperator == stringOperator

    companion object {
        const val zeroPriority = 0
        const val additionPriority = 1
        const val subtractionPriority = 1
        const val multiplicationPriority = 2
        const val divisionPriority = 2
        const val powerPriority = 3
        const val bracketPriority = 4

        fun addition() = Operator("+")
        fun subtraction() = Operator("-")
        fun multiplication() = Operator("*")
        fun division() = Operator("/")
        fun power() = Operator("^")
        fun leftBracket() = Operator("(")
        fun rightBracket() = Operator(")")
    }
}