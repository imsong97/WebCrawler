package com.ch0pp4.webcrawler.components

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ch0pp4.webcrawler.WebCrawlerApplication

class DeviceBootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, i: Intent?) {
        if (i?.action == Intent.ACTION_BOOT_COMPLETED) {
            (context?.applicationContext as? WebCrawlerApplication)?.setAlarmManager()
        }
    }
}