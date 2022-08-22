package calculator

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