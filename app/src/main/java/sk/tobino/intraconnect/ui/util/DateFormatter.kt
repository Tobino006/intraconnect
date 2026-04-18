package sk.tobino.intraconnect.ui.util

import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatDate (
    raw: String,
    locale: Locale
): String {
    val dateUtc = OffsetDateTime.parse(raw)

    // UTC conversion to local time
    val localDateTime = dateUtc.atZoneSameInstant(ZoneId.systemDefault())

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

    return localDateTime.format(formatter)
}