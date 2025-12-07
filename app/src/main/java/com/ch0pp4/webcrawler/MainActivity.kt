package com.ch0pp4.webcrawler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.ch0pp4.slack.SlackRepository
import com.ch0pp4.webcrawler.ui.theme.WebCrawlerTheme
import com.ch0pp4.webcrawler.ui.theme.main.MainView
import com.ch0pp4.webcrawler.utils.VMProvider

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