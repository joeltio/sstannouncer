package com.sst.anouncements.feed.data.database.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.sst.anouncements.feed.model.Entry
import java.util.*

@Entity(foreignKeys = [
            ForeignKey(
                entity = FeedEntity::class,
                parentColumns = ["id"],
                childColumns = ["feedId"]
            )
        ],
        indices = [Index(value = ["feedId"])])
class EntryEntity(
    @PrimaryKey val id: String,
    val feedId: String,
    val publishedDate: Date,
    val updatedDate: Date,
    val authorName: String,
    val url: String,
    val title: String,
    val content: String
) {
    constructor(entry: Entry, feedId: String): this(
        entry.id,
        feedId,
        entry.publishedDate,
        entry.updatedDate,
        entry.authorName,
        entry.url,
        entry.title,
        entry.content
    )

    fun toEntry(): Entry = Entry(
        this.id,
        this.publishedDate,
        this.updatedDate,
        this.authorName,
        this.url,
        this.title,
        this.content
    )
}