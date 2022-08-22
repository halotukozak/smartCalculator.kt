package calculator

import calculator.Operator.Companion.leftBracket
import calculator.Operator.Companion.rightBracket
import calculator.exceptions.InvalidAssignmentException
import calculator.exceptions.InvalidExpressionException
import calculator.exceptions.InvalidIdentifierException
import calculator.exceptions.UnknownCommandException
import calculator.stacks.BigIntegerStack
import calculator.stacks.InputStack
import calculator.stacks.OperatorStack
import java.math.BigInteger

fun main() {
    Calculator.run()
}

object Calculator {
    private val variables = VariableMap()

    fun run() {
        var exit = false
        while (!exit) {
            try {
                val input = readln()
                when {
                    input.isEmpty() -> continue
                    input.matches("^/[A-z]*".toRegex()) -> exit = doCommand(input)
                    input.contains("=") -> setVariable(input)
                    input.isValidVariableName() -> {
                        val value = variables.getVariable(input)
                        println(value)
                    }

                    else -> {
                        val result = calculate(input)
                        println(result)
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
        val preparedInput = prepareInput(input)
        val postfixInput = convertToPostfix(preparedInput)
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
        val smoothInput = input.removeRedundantOperators()
        val output = InputStack()
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


    private fun convertToPostfix(preparedInput: InputStack): InputStack {
        val stack = OperatorStack()
        val output = InputStack()
        preparedInput.forEach { incoming ->
            when {
                incoming.isValidVariableName() || incoming.isNumeric() -> output.push(incoming)

                incoming.isValidOperator() -> {
                    val operator = Operator(incoming)
                    when {
                        operator == rightBracket() -> {
                            while (stack.last() != leftBracket()) {
                                output.push(stack.pop())
                            }
                            stack.pop()
                        }

                        stack.isEmpty() || stack.last() == leftBracket() || operator == leftBracket() || stack.hasHigherPrecedenceThanTop(
                            operator
                        ) -> {
                            stack.push(operator)
                        }

                        stack.hasLowerOrEqualPrecedenceThanTop(operator) -> {
                            while (stack.hasLowerOrEqualPrecedenceThanTop(operator) && stack.last() != leftBracket()) {
                                output.push(stack.pop())
                            }
                            stack.push(operator)
                        }

                    }
                }
            }
        }
        while (stack.isNotEmpty()) {
            output.push(stack.pop())
        }
        return output
    }
}

private fun String.isInvalidAssignment(): Boolean = !this.replace(" ", "").matches("[A-z]+=-?[A-z0-9]+".toRegex())
private fun String.isValidVariableName(): Boolean = this.matches("[A-Za-z]+".toRegex())
private fun String.isValidOperator(): Boolean = this.matches("[+\\-*/()^]".toRegex())
internal fun String.isInvalidVariableName(): Boolean = !this.isValidVariableName()
private fun String.isNumeric(): Boolean = this.matches("-?[0-9]+(\\.[0-9]+)?".toRegex())
private fun String.removeRedundantOperators(): String {
    var temp = this.replace(" ", "")
    val replacePairs = listOf("--" to "+", "++" to "+", "+-" to "-")
    for ((old, new) in replacePairs) {
        while (temp.contains(old)) temp = temp.replace(old, new)
    }
    return temp
}

private fun Char.isValidOperator(): Boolean = this in listOf('+', '-', '*', '/', '(', ')', '^')

