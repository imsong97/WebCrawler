package com.ch0pp4.webcrawler.components

import android.content.Context
import android.webkit.WebView
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ch0pp4.slack.SlackRepository
import com.ch0pp4.slack.local.SlackPreferenceWrapper
import com.ch0pp4.webcrawler.crawler.WebCrawlerHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Calendar
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.text.ifEmpty

class SlackMessageWorker (
    private val context: Context,
    private val workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private lateinit var webViewInstance: WebView

    override suspend fun doWork(): Result {
        if (blockCrawling()) {
            return Result.success()
        }

        try {
            initCrawling()
        } catch (e: Exception) {
            destroyWebView()
            sendSlackMessage(e.message ?: "")
            e.printStackTrace()
        }

        val apiResult = withContext(Dispatchers.IO) {
            val pref = SlackPreferenceWrapper(context)
            val isNew = pref.getIsNewFlag()
            val id = pref.getExistId().ifEmpty { "value is empty" }

            val text = if (isNew) {
                "‼️New product is Detected‼️\n++new id : $id++"
            } else {
                "++same id : $id++"
            }

            sendSlackMessage(text)
        }

        return if (apiResult) Result.success() else Result.failure()
    }

    private suspend fun initCrawling() {
        withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                try {
                    val listener = object : WebCrawlerHelper.CrawlerCallback {
                        override fun getTagId(id: String) {
                            destroyWebView()
                            continuation.resume(id) // 콜백 후 코루틴 재개
                        }
                    }

                    webViewInstance = WebCrawlerHelper(listener, WebView(context)).initDameWeb()

                    continuation.invokeOnCancellation {
                        destroyWebView()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    continuation.resumeWithException(Throwable("Init Crawling Error"))
                }
            }
        }
    }

    private suspend fun sendSlackMessage(text: String): Boolean =
        SlackRepository.instance?.sendSlackMessageCoroutine(text) ?: false

    /**
     * 0~7시 대 타임 및 일요일 크롤링 block
     * */
    private fun blockCrawling(): Boolean {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val day = calendar.get(Calendar.DAY_OF_WEEK)

        return (hour in 0..6) || day == Calendar.SUNDAY
    }

    private fun destroyWebView() {
        if (::webViewInstance.isInitialized) {
            webViewInstance.destroy()
        }
    }
}