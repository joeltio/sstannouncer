package sst.com.anouncements.feed.data.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [EntryEntity::class, FeedEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class FeedDatabase : RoomDatabase() {
    abstract fun feedDao(): FeedDao

    abstract fun entryDao(): EntryDao
}
