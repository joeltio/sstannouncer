package sst.com.anouncements.feed.data

import java.util.Date

data class Feed(
    val id: String,
    val entries: List<Entry>,
    val lastUpdated: Date,
    val categories: List<String>,
    val title: String,
    val subtitle: String
) {
    override fun equals(other: Any?): Boolean =
        other is Feed &&
        id == other.id &&
        lastUpdated.compareTo(other.lastUpdated) == 0

    override fun hashCode() = (id + lastUpdated).hashCode()
}