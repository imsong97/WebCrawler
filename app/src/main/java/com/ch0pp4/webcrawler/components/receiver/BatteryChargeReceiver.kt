package com.ch0pp4.webcrawler.components.receiver

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import com.ch0pp4.webcrawler.WebCrawlerApplication

class BatteryChargeReceiver : BaseBroadCastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("BatteryChargeReceiver", "++onReceive++")
        if (context == null || !isCharging(context)) return

        receiverScope.launchWithPendingIntent {
            (context.applicationContext as WebCrawlerApplication).setAlarmManager()
        }
    }

    private fun isCharging(context: Context?): Boolean {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context?.applicationContext?.registerReceiver(null, intentFilter) ?: return false
        val plugged = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)

        val isCharging = plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS
        Log.e("BatteryChargeReceiver", "++isCharging : $isCharging++")

        return isCharging
    }
}