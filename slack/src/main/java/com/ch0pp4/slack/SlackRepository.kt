package com.ch0pp4.slack

import com.ch0pp4.slack.remote.SendSlackEntity
import com.ch0pp4.slack.remote.SlackRemoteRepository
import io.reactivex.Single
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.printStackTrace

class SlackRepository(
    private val defaultDispatcher: CoroutineDispatcher
) {

    companion object {
        @Volatile
        private var mInstance: SlackRepository? = null
        fun getInstance(defaultDispatcher: CoroutineDispatcher = Dispatchers.IO): SlackRepository {
            synchronized(SlackRepository::class.java) {
                if (mInstance == null) {
                    mInstance = SlackRepository(defaultDispatcher)
                }
                return mInstance!!
            }
        }
    }

    fun sendSlackMessage(text: String): Single<Boolean>? =
        SlackRemoteRepository.sendSlackMessage(SendSlackEntity(text = text), BuildConfig.HOOKS_KEY)
            ?.map {
                it.body()?.string() ?: ""
            }
            ?.map {
                it == "ok"
            }
            ?.onErrorReturn {
                it.printStackTrace()
                false
            }

    suspend fun sendSlackMessageCoroutine(text: String): Boolean =
        withContext(defaultDispatcher) {
            try {
                val response = SlackRemoteRepository.sendSlackMessageCoroutine(
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