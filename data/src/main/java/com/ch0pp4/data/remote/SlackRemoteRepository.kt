package com.ch0pp4.data.remote

import okhttp3.ResponseBody
import retrofit2.Response

internal object SlackRemoteRepository {
    internal suspend fun sendSlackMessageCoroutine(entity: SendSlackEntity, key: String): Response<ResponseBody>? =
        SlackAPI.instance?.mAPI?.sendSlackMessageCoroutine(key, entity)
}