package sst.com.anouncements.feed

import sst.com.anouncements.feed.data.Feed
import sst.com.anouncements.feed.data.parser.parse
import sst.com.anouncements.http.Webservice

class FeedRepository(
    private val webservice: Webservice
) {
    fun getFeed(feedURL: String): Feed {
        val data: String = webservice.get(feedURL)
        return parse(data)
    }
}