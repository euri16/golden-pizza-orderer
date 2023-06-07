package dev.eury.goldenpizza.ui_common.utils

import java.text.NumberFormat
import java.util.Locale

fun Double.getPriceFormatted(): String {
    return NumberFormat.getCurrencyInstance(Locale("en", "US")).format(this)
}