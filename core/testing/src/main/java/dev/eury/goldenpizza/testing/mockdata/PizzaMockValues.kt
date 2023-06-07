package dev.eury.goldenpizza.testing.mockdata

import dev.eury.goldenpizza.domain.models.Pizza

object PizzaMockValues {
    fun getPizza(id: Int, price: Double? = null, isSelected: Boolean = false) = Pizza(
        name = "pizza #$id",
        price = price ?: (4.0 * id),
        imageUrl = "https://golden.pizza/image/$id",
        isSelected = isSelected
    )

    fun getPizzaList(amountOfItems: Int) = (0..amountOfItems).map {
        getPizza(it)
    }
}