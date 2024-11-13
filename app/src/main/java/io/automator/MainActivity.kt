package io.automator

import android.annotation.SuppressLint
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.test.uiautomator.UiDevice
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import io.automator.service.MyWorker
import io.automator.ui.theme.AutomatorTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat

val day30 = 1000L * 60 * 24 * 30

class MainActivity : ComponentActivity() {

    val LOCATION_PERMISSION_REQUEST_CODE = 1
    var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AutomatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        if (!isIgnoringBatteryOptimizations()) {
            requestIgnoreBatteryOptimizations()
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "tag:CpuKeepRunning");
        wakeLock?.acquire(day30)

        createTask()
//        val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
//        startActivity(intent)

        // 运行时请求权限
//        if (ContextCompat.checkSelfPermission(
//                this,
//                ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//        } else {
//            // 已经有权限，启动服务
//            startForegroundService(Intent(this, BgService::class.java))
//        }

//        val intent = Intent(this, BgService::class.java)
//        startService(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予，启动前台服务
//                startForegroundService(Intent(this, BgService::class.java))
            } else {
                // 权限被拒绝，提示用户
                Toast.makeText(
                    this,
                    "Location permission is required to start the service",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun isIgnoringBatteryOptimizations(): Boolean {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(getPackageName())
    }

    fun requestIgnoreBatteryOptimizations() {
        try {
            val intent = Intent(ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.setData(Uri.parse("package:" + getPackageName()))
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun createTask() {
        // create periodic task
        val workRequest = PeriodicWorkRequest.Builder(
            MyWorker::class.java,
            15,
            java.util.concurrent.TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiresCharging(false)
                    .setRequiredNetworkType(androidx.work.NetworkType.NOT_REQUIRED)
                    .build()
            )
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)
    }

    companion object {
        var isAppInForeground = false
        var device : UiDevice? = null
    }

    override fun onResume() {
        super.onResume()
//        device = UiDevice.getInstance(Instrumentation())
        // 当有Activity进入前台时，标记应用在前台
        isAppInForeground = true
        wakeLock?.acquire(day30)
    }

    override fun onPause() {
        super.onPause()
        // 当Activity离开前台时，标记应用不在前台
        isAppInForeground = false
        wakeLock?.release()
    }
}

@SuppressLint("SimpleDateFormat")
val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    var date by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            date = formatter.format(System.currentTimeMillis())
            delay(1000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(600.dp), contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "V我50，帮你打卡",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                text = "请不要关闭这个界面",
                fontSize = 12.sp
            )
            Text(
                text = date,
                fontSize = 12.sp
            )
        }
    }
}
