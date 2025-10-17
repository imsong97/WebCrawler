package com.ch0pp4.webcrawler.components

import android.content.Context
import android.webkit.WebView
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.ch0pp4.slack.SlackRepository
import com.ch0pp4.slack.local.SlackPreferenceWrapper
import com.ch0pp4.webcrawler.crawler.WebCrawlerHelper
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SlackMessageWorker (
    private val context: Context,
    private val workerParameters: WorkerParameters
) : RxWorker(context, workerParameters) {

    override fun createWork(): Single<Result> =
        Single.create { emitter ->
            try {
                val listener = object : WebCrawlerHelper.CrawlerCallback {
                    override fun getTagId(id: String) {
                        emitter.onSuccess(id)
                    }
                }
                WebCrawlerHelper(listener, WebView(context)).initDameWeb()
            } catch (e: Exception) {
                e.printStackTrace()
                emitter.onError(Throwable("Crawling Error"))
            }
        }.subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(Schedulers.io())
        .map {
            val pref = SlackPreferenceWrapper(context)
            val isNew = pref.getIsNewFlag()
            val id = pref.getExistId().ifEmpty { "value is empty" }

            if (isNew) {
                "‼️New product is Detected‼️\n++new id : $id++"
            } else {
                "++same id : $id++"
            }
        }
        .onErrorReturn {
            it.message
        }
        .flatMap {
            SlackRepository.instance?.sendSlackMessage(it) ?: Single.just(false)
        }
        .map {
            if (it) {
                Result.success()
            } else {
                Result.failure()
            }
        }
}