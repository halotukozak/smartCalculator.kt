package calculator.Stacks

import calculator.Operator

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

    override fun last(): Operator = super.last() ?: Operator()

}