package sst.com.anouncements.feed

import sst.com.anouncements.feed.data.Feed
import sst.com.anouncements.feed.data.database.FeedDAL
import sst.com.anouncements.feed.data.parser.parse
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
}