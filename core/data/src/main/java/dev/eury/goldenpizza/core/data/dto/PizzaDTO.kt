package dev.eury.goldenpizza.core.data.dto

import dev.eury.goldenpizza.domain.models.Pizza

data class PizzaDTO(
    val name: String,
    val price: Double
)

fun PizzaDTO.toModel(imageUrl: String) = Pizza(name, price, imageUrl)