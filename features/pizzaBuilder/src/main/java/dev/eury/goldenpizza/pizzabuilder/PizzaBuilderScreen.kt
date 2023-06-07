package dev.eury.goldenpizza.pizzabuilder

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import dev.eury.goldenpizza.domain.models.Pizza
import dev.eury.goldenpizza.pizzabuilder.PizzaBuilderScreenContract.UiEffect
import dev.eury.goldenpizza.pizzabuilder.PizzaBuilderScreenContract.UiEvent
import dev.eury.goldenpizza.pizzabuilder.PizzaBuilderScreenContract.UiState
import dev.eury.goldenpizza.pizzabuilder.PizzaBuilderScreenContract.selectedPizzas
import dev.eury.goldenpizza.ui_common.theme.GoldenPizzaTheme
import dev.eury.goldenpizza.ui_common.theme.PurpleGrey40
import dev.eury.goldenpizza.ui_common.theme.PurpleGrey80
import dev.eury.goldenpizza.ui_common.theme.Typography
import dev.eury.goldenpizza.ui_common.utils.LaunchedEffectAndCollectLatest
import dev.eury.goldenpizza.ui_common.utils.getPriceFormatted
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PizzaBuilderScreen(viewModel: PizzaBuilderViewModel = hiltViewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }

    var isBuilderProcessStarted by rememberSaveable { mutableStateOf(false) }
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        val state = viewModel.viewState.collectAsStateWithLifecycle()

        AnimatedVisibility(visible = state.value.isLoading, exit = fadeOut()) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        AnimatedVisibility(visible = !state.value.isLoading, enter = fadeIn()) {
            Box(modifier = Modifier.fillMaxSize()) {
                StartBuildingButton(
                    modifier = Modifier.align(Alignment.Center),
                    isVisible = !isBuilderProcessStarted,
                    onClick = {
                        isBuilderProcessStarted = true
                    }
                )

                EffectHandler(
                    effectFlow = viewModel.effect,
                    snackbarHostState = snackbarHostState,
                    onEvent = { viewModel.processEvent(it) },
                    showSuccessDialog = { showSuccessDialog = true }
                )

                PizzaBuilderScreen(
                    uiState = state.value,
                    isVisible = isBuilderProcessStarted,
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    onEvent = { uiEvent -> viewModel.processEvent(uiEvent) }
                )
            }

            if (showSuccessDialog) {
                PizzaOrderSuccessDialog(
                    pizzas = state.value.selectedPizzas,
                    grandTotal = state.value.totalPrice,
                    onDismissRequest = {
                        viewModel.processEvent(UiEvent.ClearOrder)
                        showSuccessDialog = false
                    }
                )
            }
        }
    }
}

@Composable
private fun EffectHandler(
    effectFlow: Flow<UiEffect?>,
    snackbarHostState: SnackbarHostState,
    onEvent: (UiEvent) -> Unit,
    showSuccessDialog: () -> Unit
) {
    val currentOnEvent by rememberUpdatedState(onEvent)
    val currentShowSuccessDialog by rememberUpdatedState(showSuccessDialog)
    val context = LocalContext.current

    LaunchedEffectAndCollectLatest(
        effectFlow,
        onEffectConsumed = { currentOnEvent(UiEvent.MarkEffectAsConsumed) }
    ) { effect ->
        when (effect) {
            UiEffect.LoadFailed -> {
                val action = snackbarHostState.showSnackbar(
                    context.getString(R.string.network_issue_error),
                    actionLabel = context.getString(R.string.retry)
                )

                if (action == SnackbarResult.ActionPerformed) {
                    currentOnEvent(UiEvent.LoadPizzaData)
                }
            }

            is UiEffect.OrderPlaced -> currentShowSuccessDialog()
        }
    }
}

@Composable
private fun PizzaBuilderScreen(
    uiState: UiState,
    isVisible: Boolean,
    onEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically {
            with(density) { -40.dp.roundToPx() }
        } + expandVertically(expandFrom = Alignment.Top) + fadeIn(initialAlpha = 0.3f),
        modifier = modifier,
    ) {
        Box {
            Column(modifier = Modifier.padding(15.dp)) {
                Text(
                    stringResource(R.string.choose_up_to_2_flavors_title),
                    style = Typography.titleLarge
                )

                Spacer(modifier = Modifier.height(20.dp))

                LazyVerticalGrid(
                    contentPadding = PaddingValues(bottom = 45.dp),
                    columns = GridCells.Adaptive(minSize = 128.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = uiState.pizzas,
                        key = { pizza -> pizza.name },
                    ) { pizza ->
                        PizzaFlavorItem(
                            pizza = pizza,
                            areMaxItemsReached = uiState.areMaxSelectedItemsReached,
                            onItemSelected = {
                                onEvent(UiEvent.OnTogglePizzaSelection(it))
                            },
                        )
                    }
                }
            }

            CheckoutContainer(
                uiState.totalPrice,
                onOrderClicked = { onEvent(UiEvent.ProcessOrder) },
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}

@Composable
private fun CheckoutContainer(
    totalPrice: Double,
    onOrderClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.background(PurpleGrey40)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.your_total_is, totalPrice.getPriceFormatted()),
                modifier = Modifier.weight(1f),
                style = Typography.bodyLarge.copy(color = Color.White)
            )
            Button(
                enabled = totalPrice > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF03C778),
                    contentColor = Color.White
                ), onClick = { onOrderClicked() }) {
                Text(
                    text = stringResource(R.string.order),
                    style = Typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun PizzaFlavorItem(
    pizza: Pizza,
    onItemSelected: (Pizza) -> Unit,
    modifier: Modifier = Modifier,
    areMaxItemsReached: Boolean = false,
) {
    val borderColor by remember(pizza.isSelected) {
        mutableStateOf(if (pizza.isSelected) PurpleGrey80 else Color.Transparent)
    }

    val borderSize by remember(pizza.isSelected) {
        mutableStateOf(if (pizza.isSelected) 3.dp else 0.dp)
    }

    val padding by animateDpAsState(if (pizza.isSelected) 10.dp else 0.dp)

    Column(
        modifier = modifier
            .border(borderSize, borderColor)
            .padding(padding)
            .clickable(enabled = !areMaxItemsReached || pizza.isSelected) {
                onItemSelected(pizza)
            }
    ) {
        GlideImage(
            modifier = Modifier
                .height(180.dp)
                .clip(RoundedCornerShape(5)),
            model = pizza.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Text(text = pizza.name, style = Typography.bodyLarge)
        Text(text = pizza.price.getPriceFormatted())
    }
}

@Composable
private fun StartBuildingButton(
    isVisible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = isVisible,
        exit = slideOutVertically() + fadeOut()
    ) {
        Button(onClick = onClick) {
            Text(
                stringResource(id = R.string.start_building_btn),
                style = TextStyle.Default.copy(fontSize = 22.sp)
            )
        }
    }
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES)
private fun CheckoutContainerPreview() {
    GoldenPizzaTheme {
        CheckoutContainer(totalPrice = 14.0, onOrderClicked = {})
    }
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES)
private fun StartBuildingButtonPreview() {
    GoldenPizzaTheme {
        StartBuildingButton(true, {})
    }
}
