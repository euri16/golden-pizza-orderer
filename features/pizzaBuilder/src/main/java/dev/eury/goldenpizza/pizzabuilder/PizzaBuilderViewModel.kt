package dev.eury.goldenpizza.pizzabuilder

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.eury.goldenpizza.core.data.repositories.PizzaRepository
import dev.eury.goldenpizza.core.data.utils.ResultOperation
import dev.eury.goldenpizza.domain.models.Pizza
import dev.eury.goldenpizza.domain.usecases.GetPizzaFinalPriceUseCase
import dev.eury.goldenpizza.pizzabuilder.PizzaBuilderScreenContract.UiEffect
import dev.eury.goldenpizza.pizzabuilder.PizzaBuilderScreenContract.UiEvent
import dev.eury.goldenpizza.pizzabuilder.PizzaBuilderScreenContract.UiState
import dev.eury.goldenpizza.ui_common.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PizzaBuilderViewModel @Inject constructor(
    private val repository: PizzaRepository,
    private val getPizzaPriceUseCase: GetPizzaFinalPriceUseCase
) : BaseViewModel<UiEvent, UiState, UiEffect>() {

    init {
        loadPizzas()
    }

    private fun loadPizzas(isReloading: Boolean = false) {
        viewModelScope.launch {
            if (isReloading) {
                setState { copy(isLoading = true) }
            }
            when (val result = repository.getAllPizzas()) {
                is ResultOperation.Success -> {
                    setState { copy(pizzas = result.value, isLoading = false) }
                }

                is ResultOperation.Error -> {
                    setState { copy(isLoading = false) }
                    setEffect(UiEffect.LoadFailed)
                }
            }
        }
    }


    override fun getInitialState() = UiState()

    override fun processEvent(event: UiEvent) {
        when (event) {
            is UiEvent.OnTogglePizzaSelection -> togglePizzaSelection(event.pizza)

            UiEvent.ProcessOrder -> setEffect(UiEffect.OrderPlaced)

            UiEvent.LoadPizzaData -> loadPizzas(isReloading = true)

            UiEvent.ClearOrder -> {
                setState {
                    val newPizzas = viewState.value.pizzas.map {
                        it.copy(isSelected = false)
                    }
                    copy(
                        pizzas = newPizzas,
                        totalPrice = 0.0,
                        areMaxSelectedItemsReached = false,
                        isLoading = false
                    )
                }
            }

            UiEvent.MarkEffectAsConsumed -> markEffectAsConsumed()
        }
    }

    private fun togglePizzaSelection(selectedPizza: Pizza) {
        val currentPizzas = viewState.value.pizzas
        val areMaxItemsReached = currentPizzas.count { it.isSelected } >= MAX_ALLOWED_ITEMS
        val isSelecting = currentPizzas.find { it == selectedPizza }?.isSelected == false

        if (areMaxItemsReached && isSelecting) {
            return
        }

        val selectedPizzas: MutableList<Pizza> = mutableListOf()

        val newPizzaList = currentPizzas.map { pizza ->
            val updatedPizza =
                if (pizza == selectedPizza) pizza.copy(isSelected = !pizza.isSelected) else pizza

            updatedPizza.also { if (it.isSelected) selectedPizzas.add(it) }
        }

        setState {
            copy(
                pizzas = newPizzaList,
                totalPrice = getPizzaPriceUseCase(selectedPizzas),
                areMaxSelectedItemsReached = selectedPizzas.size >= MAX_ALLOWED_ITEMS
            )
        }
    }

    companion object {
        private const val MAX_ALLOWED_ITEMS = 2
    }
}