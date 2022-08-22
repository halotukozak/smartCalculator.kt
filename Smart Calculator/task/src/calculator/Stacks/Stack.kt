package calculator.Stacks

import calculator.EmptyStackException

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