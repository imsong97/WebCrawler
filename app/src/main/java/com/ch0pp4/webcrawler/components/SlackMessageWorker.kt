package com.ch0pp4.webcrawler.components

import android.content.Context
import android.webkit.WebView
import androidx.work.Worker
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
) : Worker(context, workerParameters) {

    override fun doWork(): Result =
        Single.fromCallable {
                // TODO url 분리
                WebCrawlerHelper("https://damestore.com/product/outlet.html?cate_no=141", null, WebView(context)).init()
            }
            .subscribeOn(AndroidSchedulers.mainThread())
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
            .blockingGet() ?: Result.success()
}