package com.ch0pp4.webcrawler.utils

import android.webkit.CookieManager
import android.webkit.WebView

fun WebView.loadPage(url: String) {
    CookieManager.getInstance().also {
        it.removeAllCookies(null)
        it.flush()
    }

    this.loadUrl(url)
}