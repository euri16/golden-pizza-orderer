package dev.eury.goldenpizza.pizzabuilder

import dev.eury.goldenpizza.domain.models.Pizza
import dev.eury.goldenpizza.ui_common.ViewEffect
import dev.eury.goldenpizza.ui_common.ViewEvent
import dev.eury.goldenpizza.ui_common.ViewState

object PizzaBuilderScreenContract {
    data class UiState(
        val pizzas: List<Pizza> = emptyList(),
        val areMaxSelectedItemsReached: Boolean = false,
        val totalPrice: Double = 0.0,
        val isLoading: Boolean = true
    ) : ViewState

    val UiState.selectedPizzas
        get() = pizzas.filter { it.isSelected }

    sealed class UiEffect : ViewEffect {
        object LoadFailed : UiEffect()
        object OrderPlaced : UiEffect()
    }

    sealed class UiEvent : ViewEvent {
        object LoadPizzaData : UiEvent()
        data class OnTogglePizzaSelection(val pizza: Pizza) : UiEvent()
        object ProcessOrder : UiEvent()
        object ClearOrder : UiEvent()
        object MarkEffectAsConsumed : UiEvent()
    }

}