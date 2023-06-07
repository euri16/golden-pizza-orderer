package dev.eury.goldenpizza.core.data.repositories

import dev.eury.goldenpizza.core.data.api.PizzaApi
import dev.eury.goldenpizza.core.data.dto.toModel
import dev.eury.goldenpizza.core.data.utils.ResultOperation
import dev.eury.goldenpizza.core.data.utils.asOperation
import dev.eury.goldenpizza.domain.models.Pizza
import kotlinx.coroutines.delay
import javax.inject.Inject

class PizzaRepository @Inject constructor(
    private val api: PizzaApi
) {

    // TODO: Not the best solution, but adding for aesthetic purposes
    private val imgMap = mapOf(
        "Mozzarella" to "https://images.pexels.com/photos/1435900/pexels-photo-1435900.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
        "Super cheese" to "https://images.pexels.com/photos/14391237/pexels-photo-14391237.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
        "Pepperoni" to "https://images.pexels.com/photos/3944311/pexels-photo-3944311.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
        "Vegetarian" to "https://images.pexels.com/photos/9792460/pexels-photo-9792460.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"
    )

    suspend fun getAllPizzas(): ResultOperation<List<Pizza>> {
        return api.getAllPizzas()
            .asOperation()
            .map { pizzaList ->
                pizzaList.map { it.toModel(imgMap[it.name] ?: "") }
            }
    }
}
