package dev.eury.goldenpizza.domain.usecases

import dev.eury.goldenpizza.domain.models.Pizza
import javax.inject.Inject

class GetPizzaFinalPriceUseCase @Inject constructor() {

    operator fun invoke(pizzas: List<Pizza>) =
        pizzas.sumOf { it.price / pizzas.size }
}
