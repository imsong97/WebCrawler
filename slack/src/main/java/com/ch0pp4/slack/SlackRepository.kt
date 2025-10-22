package com.ch0pp4.slack

import com.ch0pp4.slack.remote.SendSlackEntity
import com.ch0pp4.slack.remote.SlackRemoteRepository
import io.reactivex.Single

class SlackRepository {

    companion object {
        @Volatile
        private var mInstance: SlackRepository? = null
        val instance: SlackRepository?
            get() = synchronized(SlackRepository::class.java) {
                if (mInstance == null) {
                    mInstance = SlackRepository()
                }
                return mInstance
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