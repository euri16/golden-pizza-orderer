package dev.eury.goldenpizza.pizzabuilder

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.eury.goldenpizza.domain.models.Pizza
import dev.eury.goldenpizza.ui_common.theme.GoldenPizzaTheme
import dev.eury.goldenpizza.ui_common.theme.Typography
import dev.eury.goldenpizza.ui_common.utils.getPriceFormatted

@Composable
fun PizzaOrderSuccessDialog(
    pizzas: List<Pizza>,
    grandTotal: Double,
    onDismissRequest: () -> Unit
) {
    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        ),
        onDismissRequest = { onDismissRequest() }
    ) {

        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Column(
                Modifier
                    .padding(vertical = 30.dp, horizontal = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    modifier = Modifier
                        .size(70.dp),
                    contentScale = ContentScale.Fit,
                    painter = painterResource(id = R.drawable.ic_order_placed),
                    contentDescription = null
                )
                Text(
                    stringResource(R.string.order_confirmed),
                    style = Typography.headlineLarge
                )

                Text(stringResource(R.string.summary), style = Typography.titleLarge)

                FlavorList(pizzas)

                Spacer(Modifier.height(5.dp))

                Text(
                    stringResource(R.string.total_price, grandTotal.getPriceFormatted()),
                    style = Typography.bodyMedium
                )

                Spacer(Modifier.height(5.dp))

                Button(onClick = { onDismissRequest() }) {
                    Text(stringResource(R.string.got_it))
                }
            }
        }
    }
}

@Composable
private fun FlavorList(pizzas: List<Pizza>, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        pizzas.forEach {
            Text(it.name, style = Typography.bodyLarge)
        }
    }
}

@Composable
@Preview
private fun DialogPreview() {
    GoldenPizzaTheme {
        PizzaOrderSuccessDialog(
            listOf(
                Pizza("Mozzarella", 10.0, ""),
                Pizza("Super Cheese", 10.0, ""),
            ),
            grandTotal = 13.40,
            onDismissRequest = {}
        )
    }
}