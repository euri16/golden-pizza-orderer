package dev.eury.goldenpizza.ui_common.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

/**
 * Adds a LaunchedEffect to the composition, that will relaunch only if the flow changes.
 * This function uses flowWithLifecycle() to collect the flows in a lifecycle-aware manner.
 *
 * @param flow The [Flow] that is going to be collected.
 * @param onEffectConsumed Callback to mark the event as consumed
 * @param function The block that will get executed on flow collection
 */
@Composable
fun <T> LaunchedEffectAndCollectLatest(
    flow: Flow<T?>,
    onEffectConsumed: () -> Unit,
    function: suspend (value: T) -> Unit
) {
    val effectFlow = rememberFlowWithLifecycle(flow, LocalLifecycleOwner.current)

    LaunchedEffect(effectFlow) {
        effectFlow.mapNotNull { it }.collect { item ->
            function(item)
            onEffectConsumed()
        }
    }
}
