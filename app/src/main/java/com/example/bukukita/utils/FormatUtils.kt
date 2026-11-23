package com.example.bukukita.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val IndonesiaLocale = Locale.forLanguageTag("id-ID")

fun formatRupiah(price: Double): String {
    val format = NumberFormat.getCurrencyInstance(IndonesiaLocale)
    format.maximumFractionDigits = 0
    return format.format(price).replace("Rp", "Rp ")
}

fun formatChatTime(timestampMillis: Long): String {
    val formatter = SimpleDateFormat("HH:mm", IndonesiaLocale)
    return formatter.format(Date(timestampMillis))
}

fun formatChatDateLabel(
    timestampMillis: Long,
    todayLabel: String,
    locale: Locale = Locale.getDefault()
): String {
    val now = Calendar.getInstance()
    val target = Calendar.getInstance().apply { timeInMillis = timestampMillis }
    val isSameDay = now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
        now.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)
    if (isSameDay) return todayLabel
    val formatter = SimpleDateFormat("d MMMM yyyy", locale)
    return formatter.format(Date(timestampMillis))
}
