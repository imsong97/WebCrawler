package com.ch0pp4.data

import com.ch0pp4.data.datasource.SlackRemoteDataSource
import com.ch0pp4.data.local.AppDataStore
import com.ch0pp4.data.remote.SendSlackEntity
import com.ch0pp4.data.remote.SlackRemoteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WebCrawlerDataRepository(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val remoteDataSource: SlackRemoteDataSource = SlackRemoteRepository
) : WebCrawlerRepository {

    override suspend fun sendSlackMessageCoroutine(text: String): Boolean =
        withContext(defaultDispatcher) {
            try {
                val response = remoteDataSource.sendSlackMessageCoroutine(
                    SendSlackEntity(text = text),
                    BuildConfig.HOOKS_KEY
                )

                response?.body()?.string() == "ok"
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
}