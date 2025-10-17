package com.ch0pp4.webcrawler

import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.ch0pp4.webcrawler.crawler.WebCrawlerHelper
import com.ch0pp4.webcrawler.ui.theme.WebCrawlerTheme
import com.ch0pp4.webcrawler.utils.loadPage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebCrawlerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DameWebView()
                }
            }
        }
    }
}

@Composable
fun DameWebView() {
    val url = "https://damestore.com/product/outlet.html?cate_no=141"
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            val listener = object : WebCrawlerHelper.CrawlerCallback {
                override fun getTagId(id: String) {

                }
            }
            WebCrawlerHelper(listener = listener, webView = WebView(context)).initDameWeb()
        },
        update = { webView ->
            if (webView.url != url) {
                webView.loadPage(url)
            }
        }
    )
}