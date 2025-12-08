package com.ch0pp4.webcrawler.crawler

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.ch0pp4.webcrawler.utils.loadPage
import com.ch0pp4.webcrawler.WebCrawlerApplication.Companion.appContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WebCrawlerHelper(
    private val listener: CrawlerCallback? = null,
    private val webView: WebView,
    private val coroutineScope: CoroutineScope
) {
    private var isRedirect = false

    fun initDameWeb(): WebView {
        val url = "https://damestore.com/product/outlet.html?cate_no=141"
        val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36"
        val clientObj = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                var newUrl = request?.url.toString()

                if (newUrl.contains("m.damestore.com")) {
                    newUrl = newUrl.replace("m.damestore.com", "www.damestore.com")
                }

                view?.loadPage(newUrl)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                println("++++++++++onPageFinished+++++++++++")
                println(url)

                // 강제 리다이렉션 대응
                val jsUaOverride = """
                            Object.defineProperty(navigator, 'userAgent', {
                                get: function() { 
                                    return '$userAgent'; 
                                }
                            });
                        """.trimIndent()

                webView.evaluateJavascript(jsUaOverride) { result ->
                    println("++++++++++UA+++++++++++")
                    println(result)
                }
                val jsCode = """
                            const ulElement = document.querySelector('ul.prdList');
                            if (ulElement == null) return "";
                            
                            const firstLi = ulElement.querySelector('li');
                            
                            return firstLi ? firstLi.id : "";
                        """.trimIndent()

                webView.evaluateJavascript("(function() {$jsCode; })();") {
                    coroutineScope.launch {
                        println("+++++evaluateJavascript+++++")
                        println(it)
                        println("+++++evaluateJavascript+++++")

                        withContext(Dispatchers.IO) {
                            val pref = appContainer.appDataStore
                            val existId = pref.existId.catch { emit("") }.first()
                            val newId = it.replace("\"", "").ifEmpty { existId }

                            when {
                                existId.isEmpty() && newId.isNotEmpty() -> {
                                    // 최초 등록
                                    pref.setIsNewFlag(false)
                                    pref.setId(newId)
                                }
                                existId.isNotEmpty() && newId != existId -> {
                                    // 바뀔경우
                                    pref.setIsNewFlag(true)
                                    pref.setId(newId)
                                }
                                existId.isNotEmpty() && newId == existId -> {
                                    // 그대로
                                    pref.setIsNewFlag(false)
                                }
                            }
                        }

                        listener?.getTagId(it)
                    }
                }
            }
        }

        return init(url, clientObj)
    }

    private fun init(
        url: String,
        clientObj: WebViewClient,
    ): WebView = WebCrawler(webView, clientObj, url).init()

    interface CrawlerCallback {
        fun getTagId(id: String)
    }
}