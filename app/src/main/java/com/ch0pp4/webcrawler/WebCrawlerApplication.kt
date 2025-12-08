package com.ch0pp4.webcrawler

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.ch0pp4.webcrawler.components.TimeEventReceiver
import com.ch0pp4.webcrawler.di.AppContainer
import java.util.Calendar

class WebCrawlerApplication : Application() {

    companion object {
        lateinit var appContainer: AppContainer
    }

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(applicationContext)
    }

    @SuppressLint("ScheduleExactAlarm")
    fun setAlarmManager() {
        Log.e("WebCrawlerApplication", "++setAlarmManager++")

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = getDameWebIntent()
        val pendingIntent = getPendingIntent(intent)

        val calendar = Calendar.getInstance().apply {
            val minute = get(Calendar.MINUTE)

            if (minute < 30) {
                set(Calendar.MINUTE, 30)
            } else {
                add(Calendar.HOUR_OF_DAY, 1)
                set(Calendar.MINUTE, 0)
            }
//            add(Calendar.MINUTE, 1)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

//        alarmManager.cancel(pendingIntent)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    fun cancelAlarmManager() {
        Log.e("WebCrawlerApplication", "++cancelAlarmManager++")
        val i = getDameWebIntent()
        val pendingIntent = getPendingIntent(i)
        (getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(pendingIntent)
    }

    private fun getDameWebIntent(): Intent = Intent(this@WebCrawlerApplication, TimeEventReceiver::class.java).apply {
        this.action = "components.TimeEventReceiver.DameWeb" // action 식별
    }

    private fun getPendingIntent(i: Intent): PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.getBroadcast(this@WebCrawlerApplication, 0, i, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    } else {
        PendingIntent.getBroadcast(this@WebCrawlerApplication, 0, i, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}