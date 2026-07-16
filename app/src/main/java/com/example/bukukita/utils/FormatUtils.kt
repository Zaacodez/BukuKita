package com.example.bukukita.utils

import java.text.NumberFormat
import java.util.Locale

fun formatRupiah(price: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    format.maximumFractionDigits = 0
    return format.format(price).replace("Rp", "Rp ")
}