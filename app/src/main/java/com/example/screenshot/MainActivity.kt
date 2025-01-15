package com.example.screenshot

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val screenshotHelper = ScreenshotHelper(this)
        val data: TextView = findViewById(R.id.data)
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
}
