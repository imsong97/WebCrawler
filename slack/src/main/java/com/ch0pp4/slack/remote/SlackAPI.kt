package com.ch0pp4.slack.remote

import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

internal class SlackAPI {

    private val BASEURL: String = "https://hooks.slack.com/services/"
    val mAPI: API
    private val mRetrofit: Retrofit

    init {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor {
                val request = it.request().newBuilder()
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                val requestBuilder = request.build()
                it.proceed(requestBuilder)
            }
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .build()

        mRetrofit = Retrofit.Builder()
            .baseUrl(BASEURL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        mAPI = mRetrofit.create(API::class.java)
    }

    companion object {
        @Volatile
        private var mInstance: SlackAPI? = null

        val instance: SlackAPI?
            get() = synchronized(SlackAPI::class.java) {
                if (mInstance == null) {
                    mInstance = SlackAPI()
                }
                return mInstance
            }
    }

    interface API {
        @POST("T07C8GL2B9A/B09M13F6F0D/{hooksKey}")
        fun sendSlackMessage(
            @Path("hooksKey") key: String,
            @Body entity: SendSlackEntity
        ): Single<Response<ResponseBody>>
    }
}