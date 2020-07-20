package com.sst.anouncements.notification

import com.sst.anouncements.feed.model.Entry

interface NotificationService {
    fun setup()

    fun pushNewEntry(entry: Entry)
}