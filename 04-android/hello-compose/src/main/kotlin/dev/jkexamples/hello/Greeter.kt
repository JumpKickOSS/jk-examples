package dev.jkexamples.hello

/** The rules, in plain Kotlin: what `jk test` checks without Android. */
object Greeter {
    fun greeting(name: String): String {
        val trimmed = name.trim()
        return if (trimmed.isEmpty()) "Hello, whoever you are!" else "Hello, $trimmed!"
    }

    fun tally(count: Int): String =
        when (count) {
            0 -> "No one greeted yet"
            1 -> "1 greeting"
            else -> "$count greetings"
        }
}
