package sst.com.anouncements.feed.data

import sst.com.anouncements.feed.model.Feed
import sst.com.anouncements.feed.data.database.FeedDAL
import sst.com.anouncements.feed.data.parser.parse
import sst.com.anouncements.feed.model.Entry
import sst.com.anouncements.http.Webservice

class FeedRepository(
    private val webservice: Webservice,
    private val feedDAL: FeedDAL
) {
    fun getFeed(feedURL: String): Feed {
        if (!feedDAL.hasFeed(feedURL)) {
            val data: String = webservice.get(feedURL)
            feedDAL.overwriteFeed(parse(data), feedURL)
        }

        // Always get only from DAL, so that the data shown is
        // always representative of the database
        return feedDAL.getFeed(feedURL)
    }

    fun getNewEntries(feedURL: String): List<Entry> {
        // This is the first time the DB has seen this feed
        if (!feedDAL.hasFeed(feedURL)) {
            val data: String = webservice.get(feedURL)
            val feed = parse(data)
            feedDAL.overwriteFeed(feed, feedURL)

            // Everything is new
            return feed.entries
        }

        // Find differences between current feed and new feed
        // Preliminary check: send a HEAD request to check the last modified version
        val remoteLastModified = webservice.headLastModified(feedURL)
        val localLastModified = feedDAL.getFeedLastUpdated(feedURL).time
        // Local data is newer or the same as remote data
        if (localLastModified >= remoteLastModified) {
            return listOf()
        }

        // Get new data and compare with local data
        val data: String = webservice.get(feedURL)
        val remoteFeed = parse(data)
        val localFeed = feedDAL.getFeed(feedURL)

        val newEntries = remoteFeed.entries.filterNot { remoteEntry ->
            localFeed.entries.any { localEntry ->
                // Compare using ids
                // Comparing with every attribute is not ideal as the entry may just have been
                // updated, but it is not a new entry
                localEntry.id == remoteEntry.id
            }
        }

        // Update the local database with the remote data
        feedDAL.overwriteFeed(remoteFeed, feedURL)

        return newEntries
    }
}