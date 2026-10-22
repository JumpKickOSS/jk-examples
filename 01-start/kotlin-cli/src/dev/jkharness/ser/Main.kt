package dev.jkharness.ser

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable data class Greeting(val message: String, val count: Int = 1)

fun main() {
    // Greeting.serializer() exists only when the serialization compiler plugin ran.
    println(Json.encodeToString(Greeting.serializer(), Greeting("hello from jk")))
}
