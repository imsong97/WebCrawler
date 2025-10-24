package com.ch0pp4.webcrawler.components

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class SlackMessageWorker (
    private val context: Context,
    private val workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }
}