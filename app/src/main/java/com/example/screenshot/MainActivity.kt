package com.example.screenshot

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class MainActivity : AppCompatActivity(), Activity.ScreenCaptureCallback {

    lateinit var data: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        val screenshotHelper = ScreenshotHelper(this)
        data = findViewById(R.id.data)
        val takeScreenshotButton: Button = findViewById(R.id.takeScreenshotButton)

        takeScreenshotButton.setOnClickListener {
            lifecycleScope.launch {
                if (screenshotHelper.takeScreenshotSuccessfully(listOf(data))) Toast.makeText(
                    this@MainActivity,
                    "Screenshot saved!",
                    Toast.LENGTH_SHORT
                ).show()
                else Toast.makeText(
                    this@MainActivity, "Failed to save screenshot.", Toast.LENGTH_SHORT
                ).show()

            }
        }
    }


    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            registerScreenCaptureCallback(mainExecutor, this)
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            unregisterScreenCaptureCallback(this)
        }
    }

    override fun onScreenCaptured() {
        val screenshotHelper = ScreenshotHelper(this)

        lifecycleScope.launch {
            if (screenshotHelper.takeScreenshotSuccessfully(listOf(data))) Toast.makeText(
                this@MainActivity,
                "Screenshot saved!",
                Toast.LENGTH_SHORT
            ).show()
            else Toast.makeText(
                this@MainActivity, "Failed to save screenshot.", Toast.LENGTH_SHORT
            ).show()

        }
    }
}
