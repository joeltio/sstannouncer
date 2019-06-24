package sst.com.anouncements.feed.data

import java.util.Date

data class Entry(
    val id: String,
    val publishedDate: Date,
    val updatedDate: Date,
    val authorName: String,
    val url: String,
    val title: String,
    val content: String
) {
    override fun equals(other: Any?) =
        other is Entry &&
        id == other.id &&
        updatedDate.compareTo(other.updatedDate) == 0

    override fun hashCode() = (id + updatedDate).hashCode()
}