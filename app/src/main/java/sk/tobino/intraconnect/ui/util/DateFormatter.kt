package sk.tobino.intraconnect.ui.util

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatDate (
    raw: String,
    locale: Locale
): String {
    val date = OffsetDateTime.parse(raw)

    val formatter = when (locale.language) {
        "sk" -> DateTimeFormatter.ofPattern (
            "EEEE, d. M. yyyy 'o' HH:mm",
            locale
        )
        else -> DateTimeFormatter.ofPattern (
            "EEEE, MM/dd/yy 'at' HH:mm",
            locale
        )
    }

    return date.format(formatter)
}