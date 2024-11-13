package io.automator.service

import android.app.ActivityManager
import android.app.Instrumentation
import android.app.UiAutomation
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.test.uiautomator.UiDevice
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.automator.MainActivity


class MyWorker(val context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        // 执行任务，比如上传文件、同步数据等
        Log.i("Automator", "Task is running ${System.currentTimeMillis()}")

        if (isFeishuOpen()) {
            // press back is ok
            MainActivity.device?.pressBack()
            return Result.success()
        }

        if (!MainActivity.isAppInForeground) {
            // open app
//            val intent = Intent(applicationContext, MainActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            applicationContext.startActivity(intent)
            MainActivity.device?.pressHome()
            MainActivity.device?.pressHome()
            MainActivity.device?.click(100, 100) // app 所在位置

            val launchIntent = context.packageManager.getLaunchIntentForPackage("io.automator")
            if (launchIntent != null) {
                context.startActivity(launchIntent)
            }
            Log.i("Automator", "Open app: me")
            return Result.success()
        }

        if (isMeOpen()) return Result.success()

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        applicationContext.startActivity(intent)
        // 返回结果
        return Result.success()  // 成功执行任务
    }

    fun isMeOpen(): Boolean {
        return isAppOpen("io.automator.MainActivity")
    }

    fun isFeishuOpen() : Boolean {
        return isAppOpen("com.ss.android.lark")
    }

    fun isAppOpen(name: String) : Boolean {
        val manager = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val taskInfo = manager.getRunningTasks(1)
        if (taskInfo.isEmpty()) return false
        val appName = taskInfo[0].topActivity?.className
        Log.i("Automator", "Current activity: $appName")
        return appName?.contains(name) == true
    }
}
