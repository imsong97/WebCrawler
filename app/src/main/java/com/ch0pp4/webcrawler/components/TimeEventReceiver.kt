package com.ch0pp4.webcrawler.components

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.ch0pp4.webcrawler.WebCrawlerApplication

class TimeEventReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, i: Intent?) {
        context?.let {
//            if (i?.action == "components.TimeEventReceiver") // action 식별
            val workRequest = OneTimeWorkRequest.Builder(SlackMessageWorker::class.java).build()
            WorkManager.getInstance(it).enqueue(workRequest)

            // 반복을 위한 재등록
            (it.applicationContext as? WebCrawlerApplication)?.setAlarmManager()
        }
    }
}