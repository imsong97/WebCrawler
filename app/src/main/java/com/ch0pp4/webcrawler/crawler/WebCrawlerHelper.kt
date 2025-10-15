package com.ch0pp4.webcrawler.crawler

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.ch0pp4.webcrawler.utils.loadPage

class WebCrawlerHelper(
    private val url: String,
    private val webView: WebView
) {

    @SuppressLint("SetJavaScriptEnabled")
    fun init(): WebView =
        webView.apply {
            val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36"

            settings.userAgentString = userAgent
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            settings.setSupportZoom(true)

            webViewClient = object : WebViewClient() {
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
                        println("+++++evaluateJavascript+++++")
                        println(it)
                        println("+++++evaluateJavascript+++++")
                    }
                }
            }

            webChromeClient = WebChromeClient()
        }.also {
            it.loadPage(url)
        }
}