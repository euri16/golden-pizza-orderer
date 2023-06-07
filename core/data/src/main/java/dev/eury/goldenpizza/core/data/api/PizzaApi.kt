package dev.eury.goldenpizza.core.data.api

import dev.eury.goldenpizza.core.data.dto.PizzaDTO
import dev.eury.goldenpizza.network.calladapter.NetworkResponse
import retrofit2.http.GET

interface PizzaApi {

    @GET("pizzas.json")
    suspend fun getAllPizzas() : NetworkResponse<List<PizzaDTO>>
}
