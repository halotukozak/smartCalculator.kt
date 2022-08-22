package calculator.stacks

import calculator.Operator

class OperatorStack : Stack<Operator> {
    override val storage = mutableListOf<Operator>()
    override fun toString(): String = asString()
    override fun last(): Operator = super.last() ?: Operator()


    val hasEqualPrecedenceOnTop: (Operator) -> Boolean = { operator ->
        operator.priority == lastOperatorPriority()
    }

    val hasHigherPrecedenceThanTop: (Operator) -> Boolean = { operator ->
        operator.priority > lastOperatorPriority()
    }
    val hasLowerPrecedenceThanTop: (Operator) -> Boolean = { operator ->
        operator.priority < lastOperatorPriority()
    }

    val hasHigherOrEqualPrecedenceThanTop: (Operator) -> Boolean = { operator ->
        hasHigherPrecedenceThanTop(operator) || hasEqualPrecedenceOnTop(operator)
    }

    val hasLowerOrEqualPrecedenceThanTop: (Operator) -> Boolean = { operator ->
        hasLowerPrecedenceThanTop(operator) || hasEqualPrecedenceOnTop(operator)
    }

    val lastOperatorPriority: () -> Int = { this.last().priority }

}