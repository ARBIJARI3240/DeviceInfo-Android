// app/src/main/java/com/example/deviceinfo/MainActivity.kt
package com.example.deviceinfo

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var txtDeviceInfo: TextView
    private lateinit var txtSystemHealth: TextView
    private lateinit var txtBattery: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        txtDeviceInfo = findViewById(R.id.txtDeviceInfo)
        txtSystemHealth = findViewById(R.id.txtSystemHealth)
        txtBattery = findViewById(R.id.txtBattery)

        showDeviceInfo()
        showSystemHealth()
        showBatteryHealth()
    }

    private fun showDeviceInfo() {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        val version = Build.VERSION.RELEASE
        val cpuAbi = Build.SUPPORTED_ABIS.joinToString(", ")
        val ramInfo = getTotalRAM()
        txtDeviceInfo.text = """
            سازنده: $manufacturer
            مدل: $model
            نسخه اندروید: $version
            پردازنده: $cpuAbi
            مقدار رم: $ramInfo
        """.trimIndent()
    }

    private fun showSystemHealth() {
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        val freeMem = mi.availMem / (1024 * 1024)
        val totalMem = mi.totalMem / (1024 * 1024)

        val statFs = StatFs(Environment.getDataDirectory().path)
        val bytesAvailable = statFs.availableBytes / (1024 * 1024)
        val totalStorage = statFs.totalBytes / (1024 * 1024)

        txtSystemHealth.text = """
            رم آزاد: $freeMem مگابایت از $totalMem مگابایت
            حافظه آزاد: $bytesAvailable مگابایت از $totalStorage مگابایت
        """.trimIndent()
    }

    private fun showBatteryHealth() {
        val batteryIntent = registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

        val percent = if (level >= 0 && scale > 0) (100 * level / scale) else -1

        val health = batteryIntent?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
        val healthStr = when (health) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "سالم"
            BatteryManager.BATTERY_HEALTH_DEAD -> "خراب"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "گرم‌شده"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "ولتاژ بالا"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "خطا"
            else -> "نامشخص"
        }
        txtBattery.text = "سلامت باتری: $healthStr\nدرصد شارژ: $percent٪"
    }

    private fun getTotalRAM(): String {
        val mi = ActivityManager.MemoryInfo()
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(mi)
        val totalMegs = mi.totalMem / 1048576L
        return "$totalMegs مگابایت"
    }
}
