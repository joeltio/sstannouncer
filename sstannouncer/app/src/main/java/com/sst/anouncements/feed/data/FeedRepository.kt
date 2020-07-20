package com.sst.anouncements.feed.data

import com.sst.anouncements.feed.model.Feed
import com.sst.anouncements.feed.data.database.FeedDAL
import com.sst.anouncements.feed.data.parser.parse
import com.sst.anouncements.feed.model.Entry
import com.sst.anouncements.http.Webservice

class FeedRepository(
    private val webservice: Webservice,
    private val feedDAL: FeedDAL
) {
    private fun checkFeedIsUpToDate(feedURL: String): Boolean {
        val remoteLastModified = webservice.headLastModified(feedURL)
        val localLastModified = feedDAL.getFeedLastUpdated(feedURL).time
        // Return whether local data is newer or the same as remote data
        return (localLastModified >= remoteLastModified)
    }

    private fun forceUpdateLocalFeed(feedURL: String): Feed {
        val data: String = webservice.get(feedURL)
        val feed = parse(data)
        feedDAL.overwriteFeed(feed, feedURL)

        return feed
    }

    fun getFeed(feedURL: String): Feed {
        // Update if there is no local data
        // or there the local data is not up to date
        if (!feedDAL.hasFeed(feedURL)
            || !checkFeedIsUpToDate(feedURL)) {
            forceUpdateLocalFeed(feedURL)
        }

        // Always get only from DAL, so that the data shown is
        // always representative of the database
        return feedDAL.getFeed(feedURL)
    }

    fun getNewEntries(feedURL: String): List<Entry> {
        // This is the first time the DB has seen this feed
        if (!feedDAL.hasFeed(feedURL)) {
            val feed = forceUpdateLocalFeed(feedURL)
            // Everything is new
            return feed.entries
        }

        // Find differences between current feed and new feed
        // Preliminary check
        if (checkFeedIsUpToDate(feedURL)) {
            return listOf()
        }

        // Get new data and compare with local data
        val localFeed = feedDAL.getFeed(feedURL)
        val remoteFeed = forceUpdateLocalFeed(feedURL)

        return remoteFeed.entries.filterNot { remoteEntry ->
            localFeed.entries.any { localEntry ->
                // Compare using ids
                // Comparing with every attribute is not ideal as the entry may just have been
                // updated, but it is not a new entry
                localEntry.id == remoteEntry.id
            }
        }
    }
}