package com.ch0pp4.webcrawler.components.receiver

import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.ch0pp4.webcrawler.WebCrawlerApplication
import com.ch0pp4.webcrawler.components.SlackMessageWorker

class TimeEventReceiver : BaseBroadCastReceiver() {
    override fun onReceive(context: Context?, i: Intent?) {
        context?.let {
//            if (i?.action == "components.TimeEventReceiver") // action 식별
//            val workRequest = OneTimeWorkRequest.Builder(SlackMessageRxWorker::class.java).build()
            val workRequest = OneTimeWorkRequest.Builder(SlackMessageWorker::class.java).build()
            WorkManager.Companion.getInstance(it).enqueue(workRequest)

            // 반복을 위한 재등록
            receiverScope.launchWithPendingIntent {
                (it.applicationContext as? WebCrawlerApplication)?.setAlarmManager()
            }
        }
    }
}