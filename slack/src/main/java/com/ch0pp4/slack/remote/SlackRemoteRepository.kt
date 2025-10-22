package com.ch0pp4.slack.remote

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response

internal object SlackRemoteRepository {
    internal fun sendSlackMessage(entity: SendSlackEntity, key: String): Single<Response<ResponseBody>>? =
        SlackAPI.instance?.mAPI?.sendSlackMessage(key, entity)

    internal suspend fun sendSlackMessageCoroutine(entity: SendSlackEntity, key: String): Response<ResponseBody>? =
        SlackAPI.instance?.mAPI?.sendSlackMessageCoroutine(key, entity)
}