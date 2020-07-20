package com.sst.anouncements.feed.data.database

import com.sst.anouncements.feed.model.Feed
import java.util.*

interface FeedDAL {
    fun getFeed(feedURL: String): Feed

    fun hasFeed(feedURL: String): Boolean

    fun updateFeed(newFeed: Feed, feedURL: String)

    fun overwriteFeed(newFeed: Feed, feedURL: String)

    fun getFeedLastUpdated(feedURL: String): Date
}