package com.sst.anouncements.feed.data.database.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.sst.anouncements.feed.model.Feed
import java.util.*

@Entity(indices = [Index(value = ["url"], unique = true)])
class FeedEntity(
    @PrimaryKey val id: String,
    val url: String,
    val lastUpdated: Date
) {
    constructor(feed: Feed, feedURL: String) : this(feed.id, feedURL, feed.lastUpdated)
}
