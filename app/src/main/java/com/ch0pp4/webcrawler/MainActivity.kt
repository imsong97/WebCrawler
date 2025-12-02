package com.ch0pp4.webcrawler

import android.os.Bundle
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import com.ch0pp4.slack.SlackRepository
import com.ch0pp4.webcrawler.crawler.WebCrawlerHelper
import com.ch0pp4.webcrawler.ui.theme.WebCrawlerTheme
import com.ch0pp4.webcrawler.utils.VMProvider
import com.ch0pp4.webcrawler.utils.loadPage
import kotlinx.coroutines.CoroutineScope

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by lazy {
        val tempViewModel = MainViewModel(SlackRepository.getInstance())
        VMProvider(this@MainActivity, tempViewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebCrawlerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainView(viewModel, lifecycleScope)
                }
            }
        }
    }
}

@Composable
fun MainView(mainViewModel: MainViewModel, coroutineScope: CoroutineScope) {
    val isVisible by mainViewModel.webViewVisible.collectAsState()
    val context = LocalContext.current

    if (isVisible) {
        CrawlingWebView(mainViewModel, coroutineScope)
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    (context.applicationContext as WebCrawlerApplication).setAlarmManager()
                    Toast.makeText(context, "Start", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = stringResource(R.string.txt_init_worker))
            }
            Button(
                onClick = {
                    (context.applicationContext as WebCrawlerApplication).cancelAlarmManager()
                    Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = stringResource(R.string.txt_cancel_worker))
            }
            Button(
                onClick = {
                    mainViewModel.setWebViewVisible(true)
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = stringResource(R.string.txt_start_crawling))
            }
        }
    }
}

@Composable
fun CrawlingWebView(mainViewModel: MainViewModel, coroutineScope: CoroutineScope) {
    val url = "https://damestore.com/product/outlet.html?cate_no=141"
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            val listener = object : WebCrawlerHelper.CrawlerCallback {
                override fun getTagId(id: String) {
                    mainViewModel.sendSlackMessageCoroutine(id)
                }
            }
            WebCrawlerHelper(listener = listener, webView = WebView(context), coroutineScope).initDameWeb()
        },
        update = { webView ->
            if (webView.url != url) {
                webView.loadPage(url)
            }
        }
    )
}