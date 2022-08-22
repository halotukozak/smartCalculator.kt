package calculator.stacks

import calculator.Operator

class InputStack : Stack<String> {

    override val storage = mutableListOf<String>()
    fun push(e: Char) = push(e.toString())
    fun push(e: Operator) = push(e.toString())

    fun addToLast(e: Char) {
        storage[storage.size - 1] += e.toString()
    }
}