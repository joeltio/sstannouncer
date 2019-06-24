package sst.com.anouncements.feed.data.parser.w3dom

import java.text.SimpleDateFormat
import java.util.*

// Parse the dates when any are encountered
const val dateFormat = "yyyy-MM-dd'T'kk:mm:ss.SSSz"

fun parseDate(dateString: String): Date {
    val format = SimpleDateFormat(dateFormat, Locale.getDefault())
    return format.parse(dateString)
}