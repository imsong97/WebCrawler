package com.ch0pp4.webcrawler.components

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import io.reactivex.Single

class SlackMessageRxWorker (
    private val context: Context,
    private val workerParameters: WorkerParameters
) : RxWorker(context, workerParameters) {

    override fun createWork(): Single<Result?> {
        return Single.just(Result.success())
    }

//    private lateinit var webViewInstance: WebView
//
//    override fun createWork(): Single<Result> =
//        if (blockCrawling()) {
//            Single.just(Result.success())
//        } else {
//            Single.create { emitter ->
//                try {
//                    val listener = object : WebCrawlerHelper.CrawlerCallback {
//                        override fun getTagId(id: String) {
//                            destroyWebView()
//                            emitter.onSuccess(id)
//                        }
//                    }
//                    webViewInstance = WebCrawlerHelper(listener, WebView(context), CoroutineScope(
//                        Dispatchers.Main)).initDameWeb()
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    destroyWebView()
//                    emitter.onError(Throwable("Crawling Error"))
//                }
//            }.subscribeOn(AndroidSchedulers.mainThread())
//            .observeOn(Schedulers.io())
//            .map {
//                val pref = SlackPreferenceWrapper(context)
//                val isNew = pref.getIsNewFlag()
//                val id = pref.getExistId().ifEmpty { "value is empty" }
//
//                if (isNew) {
//                    "‼️New product is Detected‼️\n++new id : $id++"
//                } else {
//                    "++same id : $id++"
//                }
//            }
//            .onErrorReturn {
//                it.message
//            }
//            .flatMap {
//                SlackRepository.getInstance()?.sendSlackMessage(it) ?: Single.just(false)
//            }
//            .map {
//                if (it) {
//                    Result.success()
//                } else {
//                    Result.failure()
//                }
//            }
//        }
//
//    /**
//     * 0~7시 대 타임 및 일요일 크롤링 block
//     * */
//    private fun blockCrawling(): Boolean {
//        val calendar = Calendar.getInstance()
//        val hour = calendar.get(Calendar.HOUR_OF_DAY)
//        val day = calendar.get(Calendar.DAY_OF_WEEK)
//
//        return (hour in 0..6) || day == Calendar.SUNDAY
//    }
//
//    private fun destroyWebView() {
//        if (::webViewInstance.isInitialized) {
//            webViewInstance.destroy()
//        }
//    }
}