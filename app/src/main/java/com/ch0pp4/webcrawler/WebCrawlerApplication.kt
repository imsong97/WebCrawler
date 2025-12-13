package com.ch0pp4.webcrawler

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.ch0pp4.webcrawler.components.receiver.TimeEventReceiver
import com.ch0pp4.webcrawler.di.AppContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.Calendar

class WebCrawlerApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var appContainer: AppContainer
    }

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(applicationContext)
    }

    @SuppressLint("ScheduleExactAlarm")
    suspend fun setAlarmManager() {
        Log.e("WebCrawlerApplication", "++setAlarmManager++")

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = getDameWebIntent()
        val pendingIntent = getPendingIntent(intent)

//        alarmManager.cancel(pendingIntent)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, getTriggerTime().timeInMillis, pendingIntent)
    }

    fun cancelAlarmManager() {
        Log.e("WebCrawlerApplication", "++cancelAlarmManager++")
        val i = getDameWebIntent()
        val pendingIntent = getPendingIntent(i)
        (getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(pendingIntent)
    }

    private suspend fun getTriggerTime(): Calendar {
        val term = withContext(Dispatchers.IO){
            appContainer.appDataStore.workerTerm.catch { emit(-1) }.first()
        }

        val calendar = Calendar.getInstance().apply {
            when (term) {
                30 -> {
                    val minute = get(Calendar.MINUTE)

                    if (minute < 30) {
                        this@apply.set(Calendar.MINUTE, 30)
                    } else {
                        this@apply.add(Calendar.HOUR_OF_DAY, 1)
                        this@apply.set(Calendar.MINUTE, 0)
                    }
//                    this@apply.add(Calendar.MINUTE, 1)
                    this@apply.set(Calendar.SECOND, 0)
                    this@apply.set(Calendar.MILLISECOND, 0)
                }
                60 -> {
                    this@apply.add(Calendar.HOUR_OF_DAY, 1)
                    this@apply.set(Calendar.MINUTE, 0)
                    this@apply.set(Calendar.SECOND, 0)
                    this@apply.set(Calendar.MILLISECOND, 0)
                }
                else -> { // default 30m
                    val minute = get(Calendar.MINUTE)

                    if (minute < 30) {
                        this@apply.set(Calendar.MINUTE, 30)
                    } else {
                        this@apply.add(Calendar.HOUR_OF_DAY, 1)
                        this@apply.set(Calendar.MINUTE, 0)
                    }
//                    add(Calendar.MINUTE, 1)
                    this@apply.set(Calendar.SECOND, 0)
                    this@apply.set(Calendar.MILLISECOND, 0)
                }
            }

        }

        return calendar
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