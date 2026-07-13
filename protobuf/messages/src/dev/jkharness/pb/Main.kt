package dev.jkharness.pb

fun main() {
    // The `event {}` DSL is --kotlin_out codegen wrapping the --java_out message.
    val e = event {
        name = "harness"
        timestampMillis = 0
    }
    println("${e.name}@${e.timestampMillis}")
}
