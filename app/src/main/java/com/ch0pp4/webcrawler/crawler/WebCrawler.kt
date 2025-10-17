package com.ch0pp4.webcrawler.crawler

import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient

class WebCrawler(
    private val webView: WebView,
    private val webClient: WebViewClient,
    private val url: String
) {

    fun init(): WebView =
        webView.apply {
            val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36"

            this.settings.userAgentString = userAgent
            this.settings.javaScriptEnabled = true
            this.settings.domStorageEnabled = true
            this.settings.useWideViewPort = true
            this.settings.loadWithOverviewMode = true
            this.settings.builtInZoomControls = true
            this.settings.displayZoomControls = false
            this.settings.setSupportZoom(true)
            this.webChromeClient = WebChromeClient()
            this.webViewClient = webClient
        }.also {
            it.loadUrl(url)
        }
}