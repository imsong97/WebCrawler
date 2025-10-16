package com.ch0pp4.webcrawler

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.util.Log
import com.ch0pp4.webcrawler.components.TimeEventReceiver
import java.util.Calendar

class WebCrawlerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setAlarmManager()
    }

    @SuppressLint("ScheduleExactAlarm")
    fun setAlarmManager() {
        Log.e("WebCrawlerApplication", "++setAlarmManager++")

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this@WebCrawlerApplication, TimeEventReceiver::class.java).apply {
//            this.action = "components.TimeEventReceiver" // action 식별
        }

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(this@WebCrawlerApplication, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        } else {
            PendingIntent.getBroadcast(this@WebCrawlerApplication, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val calendar = Calendar.getInstance().apply {
            add(Calendar.HOUR_OF_DAY, 1)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }
}