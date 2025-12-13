package com.ch0pp4.webcrawler.components.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class BaseBroadCastReceiver : BroadcastReceiver() {
    protected val receiverScope by lazy {
        CoroutineScope(Dispatchers.IO)
    }

    override fun onReceive(context: Context?, intent: Intent?) {}

    protected fun CoroutineScope.launchWithPendingIntent(action: suspend () -> Unit) {
        // https://developer.android.com/develop/background-work/background-tasks/broadcasts?hl=ko#security-considerations
        val pendingResult = goAsync()
        this.launch {
            try {
                action()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("BaseBroadCastReceiver", "launchWithPendingIntent Exception: ${e.message}")
            } finally {
                pendingResult.finish()
            }
        }
    }
}