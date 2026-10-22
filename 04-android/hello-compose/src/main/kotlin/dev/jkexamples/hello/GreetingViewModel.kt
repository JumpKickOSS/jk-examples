package dev.jkexamples.hello

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** What the screen shows. */
data class GreetingState(val name: String = "", val greeting: String = "", val count: Int = 0)

/** Holds the screen's state across configuration changes; the screen only renders it. */
class GreetingViewModel : ViewModel() {
    private val state = MutableStateFlow(GreetingState())
    val ui: StateFlow<GreetingState> = state.asStateFlow()

    fun onNameChange(name: String) = state.update { it.copy(name = name) }

    fun greet() = state.update { it.copy(greeting = Greeter.greeting(it.name), count = it.count + 1) }
}
