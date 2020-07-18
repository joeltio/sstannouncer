package sst.com.anouncements.feed.data.database.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import java.util.*

@Dao
interface FeedDao {
    @Insert(onConflict = REPLACE)
    fun saveFeed(feed: FeedEntity)

    @Query("SELECT COUNT(*) FROM feedentity WHERE url = :feedUrl ")
    fun countFeed(feedUrl: String): Int

    @Query("SELECT * FROM feedentity WHERE url = :feedUrl ")
    fun getFeed(feedUrl: String): FeedEntity

    @Query("SELECT lastUpdated FROM feedentity WHERE url = :feedUrl")
    fun getFeedLastUpdated(feedUrl: String): Date
}