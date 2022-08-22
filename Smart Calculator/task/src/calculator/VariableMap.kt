package calculator

import calculator.exceptions.InvalidAssignmentException
import calculator.exceptions.UnknownVariableException
import java.math.BigInteger

class VariableMap(vararg args: Pair<String, BigInteger>) : MutableMap<String, BigInteger> by mutableMapOf(*args) {
    fun getVariable(name: String): BigInteger {
        if (name.isInvalidVariableName()) throw InvalidAssignmentException()
        return this[name] ?: throw UnknownVariableException()
    }
}