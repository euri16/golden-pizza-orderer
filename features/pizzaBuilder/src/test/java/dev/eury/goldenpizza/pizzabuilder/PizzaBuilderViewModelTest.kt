package dev.eury.goldenpizza.pizzabuilder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import dev.eury.goldenpizza.core.data.repositories.PizzaRepository
import dev.eury.goldenpizza.core.data.utils.ResultOperation
import dev.eury.goldenpizza.core.data.utils.ResultOperation.Companion.wrapSuccess
import dev.eury.goldenpizza.domain.usecases.GetPizzaFinalPriceUseCase
import dev.eury.goldenpizza.pizzabuilder.PizzaBuilderScreenContract.UiEffect
import dev.eury.goldenpizza.pizzabuilder.PizzaBuilderScreenContract.UiEvent
import dev.eury.goldenpizza.testing.mockdata.PizzaMockValues
import dev.eury.goldenpizza.testing.rules.MainCoroutineRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class PizzaBuilderViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private var viewModel: PizzaBuilderViewModel? = null

    private val repository: PizzaRepository = mockk()
    private val useCase: GetPizzaFinalPriceUseCase = mockk()

    @Test
    fun `test pizza list loaded successfully`() = runTest {
        val pizzaList = PizzaMockValues.getPizzaList(4)

        coEvery { repository.getAllPizzas() } returns pizzaList.wrapSuccess()

        viewModel = PizzaBuilderViewModel(repository, useCase)

        viewModel?.viewState?.test {
            val state = awaitItem()
            assertEquals(pizzaList, state.pizzas)
            assertEquals(false, state.isLoading)
        }
    }

    @Test
    fun `test pizza list call errored`() = runTest {
        coEvery { repository.getAllPizzas() } returns ResultOperation.Error(null, null)

        viewModel = PizzaBuilderViewModel(repository, useCase)

        viewModel?.effect?.test {
            assertEquals(UiEffect.LoadFailed, awaitItem())
        }

        viewModel?.viewState?.test {
            assertEquals(false, awaitItem().isLoading)
        }
    }

    @Test
    fun `test selecting pizza`() = runTest {
        // GIVEN
        val pizzaList = PizzaMockValues.getPizzaList(20)
        coEvery { repository.getAllPizzas() } returns pizzaList.wrapSuccess()
        every { useCase.invoke(any()) } returns 0.0

        with(PizzaBuilderViewModel(repository, useCase)) {
            // WHEN
            processEvent(UiEvent.OnTogglePizzaSelection(PizzaMockValues.getPizza(1)))

            // THEN
            viewState.test {
                val selectedPizzas = awaitItem().pizzas.filter { it.isSelected }

                assertEquals(PizzaMockValues.getPizza(1, isSelected = true), selectedPizzas[0])
                assertEquals(1, selectedPizzas.size)
            }
        }
    }

    @Test
    fun `test unselecting pizza`() = runTest {
        // GIVEN
        val pizzaList = PizzaMockValues.getPizzaList(20).map {
            if(it.name == "pizza #1") it.copy(isSelected = true) else it
        }

        coEvery { repository.getAllPizzas() } returns pizzaList.wrapSuccess()
        every { useCase.invoke(any()) } returns 0.0

        with(PizzaBuilderViewModel(repository, useCase)) {
            // WHEN
            processEvent(UiEvent.OnTogglePizzaSelection(PizzaMockValues.getPizza(1, isSelected = true)))

            // THEN
            viewState.test {
                val selectedPizzas = awaitItem().pizzas.filter { it.isSelected }

                assertEquals(0, selectedPizzas.size)
            }
        }
    }

    @Test
    fun `test order placed effect`() = runTest {
        // GIVEN
        val pizzaList = PizzaMockValues.getPizzaList(1)

        coEvery { repository.getAllPizzas() } returns pizzaList.wrapSuccess()

        with(PizzaBuilderViewModel(repository, useCase)) {
            // WHEN
            processEvent(UiEvent.ProcessOrder)

            // THEN
            effect.test {
                assertEquals(UiEffect.OrderPlaced, awaitItem())
            }
        }
    }

    @Test
    fun `test reload data`() = runTest {
        coEvery { repository.getAllPizzas() } returns ResultOperation.Error(null, null)

        with(PizzaBuilderViewModel(repository, useCase)) {

            effect.test {
                assertEquals(UiEffect.LoadFailed, awaitItem())
            }

            val pizzaList = PizzaMockValues.getPizzaList(1)

            coEvery { repository.getAllPizzas() } returns pizzaList.wrapSuccess()

            // WHEN
            processEvent(UiEvent.LoadPizzaData)

            // THEN
            viewState.test {
                assertEquals(pizzaList, awaitItem().pizzas)
            }
        }
    }

    @Test
    fun `test clearing order`() = runTest {
        // GIVEN
        val pizzaList = PizzaMockValues.getPizzaList(20).map {
            if(it.name == "pizza #1") it.copy(isSelected = true) else it
        }

        coEvery { repository.getAllPizzas() } returns pizzaList.wrapSuccess()
        every { useCase.invoke(any()) } returns 0.0

        with(PizzaBuilderViewModel(repository, useCase)) {
            // WHEN
            processEvent(UiEvent.ClearOrder)

            // THEN
            viewState.test {
                val selectedPizzas = awaitItem().pizzas.filter { it.isSelected }

                assertEquals(0, selectedPizzas.size)
            }
        }
    }

    @Test
    fun `test mark effect as consumed`() = runTest {
        coEvery { repository.getAllPizzas() } returns ResultOperation.Error(null, null)

        viewModel = PizzaBuilderViewModel(repository, useCase)

        viewModel?.effect?.test {
            assertEquals(UiEffect.LoadFailed, awaitItem())
        }

        viewModel?.processEvent(UiEvent.MarkEffectAsConsumed)

        viewModel?.effect?.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun `test attempting to select more than 2 pizzas`() = runTest {
        // GIVEN
        val pizzaList = PizzaMockValues.getPizzaList(20).map {
            if(it.name == "pizza #1") it.copy(isSelected = true) else it
        }

        coEvery { repository.getAllPizzas() } returns pizzaList.wrapSuccess()
        every { useCase.invoke(any()) } returns 0.0

        with(PizzaBuilderViewModel(repository, useCase)) {
            // WHEN
            processEvent(UiEvent.OnTogglePizzaSelection(PizzaMockValues.getPizza(2)))

            val expectedSelected = listOf(
                PizzaMockValues.getPizza(1, isSelected = true),
                PizzaMockValues.getPizza(2, isSelected = true)
            )

            // THEN
            viewState.test {
                val selectedPizzas = awaitItem().pizzas.filter { it.isSelected }

                assertEquals(expectedSelected, selectedPizzas)
                assertEquals(2, selectedPizzas.size)
            }

            // WHEN
            processEvent(UiEvent.OnTogglePizzaSelection(PizzaMockValues.getPizza(3)))

            // THEN
            viewState.test {
                val selectedPizzas = awaitItem().pizzas.filter { it.isSelected }

                assertEquals(expectedSelected, selectedPizzas)
                assertEquals(2, selectedPizzas.size)
            }
        }
    }
}
