package sst.com.anouncements.feed.data.database

import sst.com.anouncements.feed.data.Feed

interface FeedDAL {
    fun getFeed(feedURL: String): Feed

    fun hasFeed(feedURL: String): Boolean

    fun updateFeed(newFeed: Feed, feedURL: String)

    fun overwriteFeed(newFeed: Feed, feedURL: String)
}