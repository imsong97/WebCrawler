package com.ch0pp4.webcrawler.components

import android.content.Context
import android.webkit.WebView
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ch0pp4.webcrawler.crawler.WebCrawlerHelper
import com.ch0pp4.webcrawler.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Calendar
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.text.ifEmpty

class SlackMessageWorker (
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private lateinit var webViewInstance: WebView
    private val coroutineMainScope = CoroutineScope(Dispatchers.Main)
    private val appContainer by lazy {
        AppContainer(this@SlackMessageWorker.context.applicationContext)
    }

    override suspend fun doWork(): Result {
        if (blockCrawling()) {
            return Result.success()
        }

        try {
            initCrawling()
        } catch (e: Exception) {
            sendSlackMessage(e.message ?: "")
            e.printStackTrace()
            return Result.failure()
        }

        val apiResult = try {
            val text = withContext(Dispatchers.IO) {
                val pref = appContainer.appDataStore
                val isNew = pref.isNewFlag.catch { emit(false) }.first()
                val id = pref.existId.catch { emit("") }.first().ifEmpty { "value is empty" }

                if (isNew) {
                    "‼️New product is Detected‼️\n++new id : $id++"
                } else {
                    "++same id : $id++"
                }
            }

            sendSlackMessage(text)
        } catch (e: Exception) {
            e.printStackTrace()
            sendSlackMessage("API Failed: ${e.message ?: "Unknown Error"}")
            false
        }

        return if (apiResult) Result.success() else Result.failure()
    }

    private suspend fun initCrawling() {
        withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                try {
                    val listener = object : WebCrawlerHelper.CrawlerCallback {
                        override fun getTagId(id: String) {
                            continuation.resume(id) // 콜백 후 코루틴 재개
                        }
                    }

                    webViewInstance = WebCrawlerHelper(
                        listener,
                        WebView(context),
                        coroutineMainScope
                    ).initDameWeb()

                    continuation.invokeOnCancellation {
                        destroyComponents()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    continuation.resumeWithException(e)
                }
            }
            destroyComponents()
        }
    }

    private suspend fun sendSlackMessage(text: String): Boolean =
        appContainer.webCrawlerDataRepository.sendSlackMessageCoroutine(text)

    /**
     * 0~7시 대 타임 및 일요일 크롤링 block
     * */
    private fun blockCrawling(): Boolean {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val day = calendar.get(Calendar.DAY_OF_WEEK)

        return (hour in 0..6) || day == Calendar.SUNDAY
    }

    private fun destroyComponents() {
        coroutineMainScope.cancel()
        if (::webViewInstance.isInitialized) {
            webViewInstance.destroy()
        }
    }
}