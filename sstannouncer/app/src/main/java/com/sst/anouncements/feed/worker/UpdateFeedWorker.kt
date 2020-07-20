package com.sst.anouncements.feed.worker

import android.content.Context
import androidx.work.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import com.sst.anouncements.feed.data.FeedRepository
import com.sst.anouncements.notification.NotificationService
import java.util.concurrent.TimeUnit

const val UPDATE_FEED_WORK_NAME = "UPDATE_FEED_PERIODICAL"
const val WORKER_INPUT_FEED_URL = "feedURL"

class UpdateFeedWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams), KoinComponent {
    companion object {
        fun createWorkRequest(feedURL: String): PeriodicWorkRequest {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val inputData = Data.Builder()
            inputData.putString(WORKER_INPUT_FEED_URL, feedURL)

            return PeriodicWorkRequestBuilder<UpdateFeedWorker>(15, TimeUnit.MINUTES)
                .setInputData(inputData.build())
                .setConstraints(constraints)
                .build()
        }
    }
    override fun doWork(): Result {
        val feedURL = inputData.getString(WORKER_INPUT_FEED_URL)!!
        val feedRepository: FeedRepository by inject()
        val notificationService: NotificationService by inject()

        val newEntries = feedRepository.getNewEntries(feedURL)
        newEntries.map {
            notificationService.pushNewEntry(it)
        }

        return Result.success()
    }
}