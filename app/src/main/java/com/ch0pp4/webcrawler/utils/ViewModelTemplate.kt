package com.ch0pp4.webcrawler.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

@Suppress("UNCHECKED_CAST")
fun <T: ViewModel> VMProvider(
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
