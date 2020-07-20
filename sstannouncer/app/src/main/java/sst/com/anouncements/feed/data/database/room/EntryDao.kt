package sst.com.anouncements.feed.data.database.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface EntryDao {
    @Insert(onConflict = REPLACE)
    fun insertEntries(entries: List<EntryEntity>)

    @Query("SELECT * FROM entryentity WHERE feedId = :feedId")
    fun getFeedEntries(feedId: String): List<EntryEntity>
}