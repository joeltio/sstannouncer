package sst.com.anouncements.feed.data.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import sst.com.anouncements.feed.data.Entry
import sst.com.anouncements.feed.data.Feed

@Database(entities = [EntryEntity::class, FeedEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class FeedDatabase : RoomDatabase() {
    abstract fun feedDao(): FeedDao

    abstract fun entryDao(): EntryDao
}
