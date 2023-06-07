package dev.eury.goldenpizza.domain.models

data class Pizza(
    val name: String,
    val price: Double,
    val imageUrl: String,
    val isSelected: Boolean = false
)
