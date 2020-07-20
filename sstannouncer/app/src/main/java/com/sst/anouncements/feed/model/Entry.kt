package com.sst.anouncements.feed.model

import android.os.Parcelable
import android.text.format.DateUtils
import kotlinx.android.parcel.Parcelize
import org.apache.commons.text.StringEscapeUtils
import java.util.*

@Parcelize
data class Entry(
    val id: String,
    val publishedDate: Date,
    val updatedDate: Date,
    val authorName: String,
    val url: String,
    val title: String,
    val content: String
) : Parcelable {
    val relativePublishedDate: String by lazy { relativeDate(publishedDate) }

    val contentWithoutHTML: String by lazy {
        // Remove any content in style tags
        var content = this.content.replace("<style[^>]*>.*</style>".toRegex(), "")
        // Remove HTML tags
        content = content.replace("<[^>]*>".toRegex(), "")
        // Unescape characters, e.g. converting &lt; to <
        StringEscapeUtils.unescapeHtml4(content).trim()
    }

    private fun relativeDate(date: Date): String {
        return DateUtils.getRelativeTimeSpanString(date.time).toString()
    }

    override fun equals(other: Any?) =
        other is Entry &&
        id == other.id &&
        updatedDate.compareTo(other.updatedDate) == 0

    override fun hashCode() = (id + updatedDate).hashCode()
}