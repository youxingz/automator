package io.automator.service

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.WorkManager
import androidx.work.PeriodicWorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.automator.MainActivity
import io.automator.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random


class BgService : Service() {

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()
        Log.d("MyBackgroundService", "Service Created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MyBackgroundService", "Service Started")

//        val randomId = Random(1000).nextInt(9999999)
//        val serviceId = "channel$randomId"
//        val name = "你就说6不6吧"
//
//        // 在服务中创建一个前台通知，确保服务不容易被系统终止
//        val channel = NotificationChannel(
//            serviceId,
//            name,
//            NotificationManager.IMPORTANCE_DEFAULT
//        )
//        val notificationManager = getSystemService(NotificationManager::class.java)
//        notificationManager.createNotificationChannel(channel)
//
//        val notification: Notification = NotificationCompat.Builder(this, serviceId)
//            .setContentTitle(name)
//            .setContentText("这个通知不能关，关了就关了")
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .build()
//
//        // 将Service设置为前台服务
//        startForeground(1, notification)
//        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        manager.notify(1, notification)

        // 这里可以执行需要常驻后台的任务，比如网络请求、长时间运行的计算等
        // 如果任务完成后还需要继续运行，可以返回 START_STICKY 或 START_REDELIVER_INTENT 等标志
        return START_STICKY // 或 START_REDELIVER_INTENT，根据需求来选择
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // 不需要绑定服务时返回 null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MyBackgroundService", "Service Destroyed")
    }
}
