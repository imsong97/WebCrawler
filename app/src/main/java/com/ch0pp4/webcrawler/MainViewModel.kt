package com.ch0pp4.webcrawler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ch0pp4.slack.SlackRepository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val slackRepository: SlackRepository
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val _webViewVisible = MutableStateFlow(false)
    val webViewVisible: StateFlow<Boolean> = _webViewVisible

    fun setWebViewVisible(visible: Boolean) {
        _webViewVisible.value = visible
    }

    fun sendSlackMessage(id: String) {
        Single.just("++in app crawling : $id++")
            .subscribeOn(Schedulers.io())
            .flatMap {
                slackRepository.sendSlackMessage(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                setWebViewVisible(false)
            }, {
                it.printStackTrace()
                setWebViewVisible(false)
            }).also {
                compositeDisposable.add(it)
            }
    }

    fun sendSlackMessageCoroutine(id: String) {
        viewModelScope.launch { // Dispatchers.Main
            slackRepository.sendSlackMessageCoroutine("++in app crawling : $id++")
            setWebViewVisible(false)
        }
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}