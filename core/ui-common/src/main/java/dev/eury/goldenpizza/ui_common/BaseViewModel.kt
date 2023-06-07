package dev.eury.goldenpizza.ui_common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface ViewState
interface ViewEvent
interface ViewEffect

abstract class BaseViewModel<Event : ViewEvent, UiState : ViewState, Effect : ViewEffect> :
    ViewModel() {

    private val _initialState: UiState by lazy { getInitialState() }

    private val _viewState = MutableStateFlow(_initialState)
    val viewState = _viewState.asStateFlow()

    private val _effect = MutableStateFlow<Effect?>(null)
    val effect: Flow<Effect?> = _effect.asStateFlow()

    abstract fun processEvent(event: Event)

    protected fun setState(reducer: UiState.() -> UiState) {
       _viewState.value = viewState.value.reducer()
    }

    protected fun setEffect(effect: Effect) {
        _effect.value = effect
    }

    fun markEffectAsConsumed() {
        _effect.value = null
    }


    abstract fun getInitialState(): UiState
}