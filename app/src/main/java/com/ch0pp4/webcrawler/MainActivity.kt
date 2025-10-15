package com.ch0pp4.webcrawler

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.ch0pp4.webcrawler.ui.theme.WebCrawlerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebCrawlerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WebViewScreen(url = "https://damestore.com/product/outlet.html?cate_no=141")
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(url: String) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
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

                        view?.loadUrl(newUrl)
                        return true
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        println("++++++++++onPageFinished+++++++++++")
                        println(url)

                        val jsUaOverride = """
                            Object.defineProperty(navigator, 'userAgent', {
                                get: function() { 
                                    return '$userAgent'; 
                                }
                            });
                        """.trimIndent()

                        view?.evaluateJavascript(jsUaOverride) { result ->
                            println("++++++++++evaluateJavascript+++++++++++")
                            println(result)
                        }
                    }
                }

                webChromeClient = WebChromeClient()
                loadUrl(url)
            }
        },
        update = { webView ->
            if (webView.url != url) {
                webView.loadUrl(url)
            }
        }
    )
}