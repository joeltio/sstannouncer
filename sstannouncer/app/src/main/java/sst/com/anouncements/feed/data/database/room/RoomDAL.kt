package sst.com.anouncements.feed.data.database.room

import sst.com.anouncements.feed.model.Feed
import sst.com.anouncements.feed.data.database.FeedDAL

class RoomDAL(private val database: FeedDatabase) : FeedDAL {
    override fun getFeed(feedURL: String): Feed {
        // Retrieve feed and entry entities
        val feedEntity = database.feedDao().getFeed(feedURL)
        val entryEntities = database.entryDao().getFeedEntries(feedEntity.id)

        // Convert to Feed and Entry objects
        val entries = List(entryEntities.size) { entryEntities[it].toEntry() }
        return Feed(
            feedEntity.id,
            entries,
            feedEntity.lastUpdated,
            // The rest of the fields are not persisted
            listOf(), "", ""
        )
    }

    override fun hasFeed(feedURL: String): Boolean = database.feedDao().countFeed(feedURL) == 1

    override fun updateFeed(newFeed: Feed, feedURL: String) {
        val feedEntity = database.feedDao().getFeed(feedURL)
        if (feedEntity.id == newFeed.id
                && feedEntity.lastUpdated.compareTo(newFeed.lastUpdated) == 0) {
            // Nothing has been updated so don't do anything
            return
        }

        // This is not very efficient, but for small number of entries, it is acceptable
        overwriteFeed(newFeed, feedURL)
    }

    override fun overwriteFeed(newFeed: Feed, feedURL: String) {
        // Do not check for any existing objects
        database.feedDao().saveFeed(FeedEntity(newFeed, feedURL))

        val entryEntities = List(newFeed.entries.size) {
            EntryEntity(newFeed.entries[it], newFeed.id)
        }
        database.entryDao().insertEntries(entryEntities)
    }
}