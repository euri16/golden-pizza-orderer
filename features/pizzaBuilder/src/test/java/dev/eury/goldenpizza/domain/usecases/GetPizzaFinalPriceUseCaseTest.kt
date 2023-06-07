package dev.eury.goldenpizza.domain.usecases

import dev.eury.goldenpizza.testing.mockdata.PizzaMockValues
import junit.framework.TestCase.assertEquals
import org.junit.Test

internal class GetPizzaFinalPriceUseCaseTest {

    val useCase = GetPizzaFinalPriceUseCase()

    @Test
    fun `test calculation with single pizza`() {
        val pizza = listOf(PizzaMockValues.getPizza(1))

        val finalPrice = useCase(pizza)

        assertEquals(4.0, finalPrice)
    }

    @Test
    fun `test calculation with 2 pizzas`() {
        val pizza = listOf(
            PizzaMockValues.getPizza(1),
            PizzaMockValues.getPizza(3)
        )

        val finalPrice = useCase(pizza)

        assertEquals(8.0, finalPrice)
    }

    @Test
    fun `test calculation with 2 pizzas, floating values`() {
        val pizza = listOf(
            PizzaMockValues.getPizza(1, price = 10.75),
            PizzaMockValues.getPizza(3, price = 11.65)
        )

        val finalPrice = useCase(pizza)

        assertEquals(11.2, finalPrice)
    }
}