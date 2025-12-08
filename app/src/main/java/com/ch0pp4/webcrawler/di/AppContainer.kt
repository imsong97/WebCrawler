package com.ch0pp4.webcrawler.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.ch0pp4.data.WebCrawlerDataRepository
import com.ch0pp4.data.local.AppDataStore
import com.ch0pp4.webcrawler.MainViewModel

class AppContainer(
    private val context: Context
) {
    // data
    val appDataStore = AppDataStore(context)
    val webCrawlerDataRepository = WebCrawlerDataRepository()

    // presentation
    val mainViewModel: (viewModelStoreOwner: ViewModelStoreOwner) -> MainViewModel = { owner ->
        val tempViewModel = MainViewModel(webCrawlerDataRepository)
        VMProvider(owner, tempViewModel)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T: ViewModel> VMProvider(
        owner: ViewModelStoreOwner,
        instance: ViewModel
    ): T {
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return if (modelClass.isAssignableFrom(instance.javaClass)) {
                    instance as T
                } else {
                    throw IllegalArgumentException()
                }
            }
        }
        return ViewModelProvider(owner, factory).get((instance as T).javaClass)
    }
}

