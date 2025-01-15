package com.example.screenshot

import android.app.Activity
import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class ScreenshotHelper(private val activity: Activity) {

    /**
     * Takes a screenshot while hiding specific views.
     * @param hiddenViews List of views to hide during the screenshot.
     * @return Boolean indicating whether the screenshot was saved successfully.
     */
    suspend fun takeScreenshotSuccessfully(hiddenViews: List<View>): Boolean {
        return withContext(Dispatchers.Main) {
            try {
                // Hide specified views
                hiddenViews.forEach { it.visibility = View.INVISIBLE }

                // Temporarily disable FLAG_SECURE
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)

                // Capture the screenshot
                val screenshot = captureScreen()

                // Re-enable FLAG_SECURE
                activity.window.setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE
                )

                // Show the hidden views again
                hiddenViews.forEach { it.visibility = View.VISIBLE }

                // Save the screenshot
                screenshot?.let { saveScreenshot(it) } ?: false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    /**
     * Captures the current screen as a Bitmap.
     * @return Captured Bitmap or null if the capture fails.
     */
    private fun captureScreen(): Bitmap? {
        return try {
            val view: View = activity.window.decorView.rootView
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)
            view.draw(canvas)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Saves the screenshot to the device storage.
     * @param bitmap The Bitmap to save.
     * @return Boolean indicating success or failure.
     */
    private fun saveScreenshot(bitmap: Bitmap): Boolean {
        val fileName = "screenshot_${System.currentTimeMillis()}.png"
        var saved = false

        val outputStream: OutputStream?
        try {
            outputStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + "/Screenshots"
                    )
                }
                val uri = activity.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                activity.contentResolver.openOutputStream(uri!!)
            } else {
                val screenshotsDir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "Screenshots"
                )
                if (!screenshotsDir.exists()) {
                    screenshotsDir.mkdirs()
                }
                val file = File(screenshotsDir, fileName)
                FileOutputStream(file)
            }

            if (outputStream != null)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream?.close()
            saved = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return saved
    }
}
