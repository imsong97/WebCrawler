package com.ch0pp4.webcrawler.components.receiver

import android.content.Context
import android.content.Intent
import com.ch0pp4.webcrawler.WebCrawlerApplication

class DeviceBootReceiver: BaseBroadCastReceiver() {
    override fun onReceive(context: Context?, i: Intent?) {
        if (i?.action == Intent.ACTION_BOOT_COMPLETED) {
            receiverScope.launchWithPendingIntent {
                (context?.applicationContext as? WebCrawlerApplication)?.setAlarmManager()
            }
        }
    }
}