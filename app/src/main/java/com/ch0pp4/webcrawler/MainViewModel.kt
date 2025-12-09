package com.ch0pp4.webcrawler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ch0pp4.data.WebCrawlerRepository
import com.ch0pp4.data.local.AppDataStore
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val crawlerRepository: WebCrawlerRepository,
    private val appDataStore: AppDataStore
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val _webViewVisible = MutableStateFlow(false)
    val webViewVisible: StateFlow<Boolean> = _webViewVisible

    fun setWebViewVisible(visible: Boolean) {
        _webViewVisible.value = visible
    }

//    fun sendSlackMessage(id: String) {
//        Single.just("++in app crawling : $id++")
//            .subscribeOn(Schedulers.io())
//            .flatMap {
//                webCrawlerDataRepository.sendSlackMessage(it)
//            }
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({
//                setWebViewVisible(false)
//            }, {
//                it.printStackTrace()
//                setWebViewVisible(false)
//            }).also {
//                compositeDisposable.add(it)
//            }
//    }

    fun sendSlackMessageCoroutine(id: String) {
        viewModelScope.launch { // Dispatchers.Main
            crawlerRepository.sendSlackMessageCoroutine("++in app crawling : $id++")
            setWebViewVisible(false)
        }
    }

    fun setCrawlingCycle(minute: Int) {
        viewModelScope.launch {
            appDataStore.setWorkerTerm(minute)
        }
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}