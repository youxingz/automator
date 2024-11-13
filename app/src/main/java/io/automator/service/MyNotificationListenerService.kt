package io.automator.service

import android.app.Instrumentation
import android.content.ComponentName
import android.content.Intent
import android.os.Looper
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.test.uiautomator.UiDevice
import io.automator.MainActivity
import java.util.logging.Handler


class MyNotificationListenerService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // 获取通知内容
        val notificationText = sbn.notification.extras.getString("android.text")
        Log.i("Automator", "Notification posted: $notificationText")
        if (notificationText != null) {
            if (notificationText.uppercase().contains("V50")) {
                // 打开 app
                val intent = packageManager.getLaunchIntentForPackage("com.ss.android.lark")
                intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                Log.i("Automator", "Detect app: ${sbn.packageName}, open: lark")

                try {
                    val device = UiDevice.getInstance(Instrumentation())
                    Thread.sleep(3000)
                    device.pressBack()
                } catch (e: Exception) {
                    e.printStackTrace()
                    android.os.Handler(Looper.getMainLooper()).postDelayed({
                        val homeIntent = Intent(applicationContext, MainActivity::class.java)
                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(homeIntent)
                        Log.i("Automator", "Back to home")
                    }, 4000)
                }

            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // 处理通知被移除时的情况
    }

    override fun onRebind(intent: Intent?) {
        requestRebind(
            ComponentName(
                this,
                NotificationListenerService::class.java
            )
        )
        super.onRebind(intent)
    }
}