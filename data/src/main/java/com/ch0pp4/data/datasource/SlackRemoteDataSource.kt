package com.ch0pp4.data.datasource

import com.ch0pp4.data.remote.SendSlackEntity
import okhttp3.ResponseBody
import retrofit2.Response

interface SlackRemoteDataSource {
    suspend fun sendSlackMessageCoroutine(entity: SendSlackEntity, key: String): Response<ResponseBody>?
}