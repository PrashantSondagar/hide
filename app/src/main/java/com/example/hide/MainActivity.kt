package com.example.hide
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.util.Log

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_MANAGE_STORAGE = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Example button for testing
        val enterGalleryButton: Button = findViewById(R.id.enterGalleryButton)
        enterGalleryButton.setOnClickListener {
            // Handle your action, e.g., navigating to another activity
            val intent = Intent(this, PinLockActivity::class.java)
            startActivity(intent)
        }

        // Check for necessary permissions on app start
        checkPermissions()
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 and above: MANAGE_EXTERNAL_STORAGE permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    try {
                        val intent = Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        intent.addCategory("android.intent.category.DEFAULT")
                        intent.data = android.net.Uri.parse("package:$packageName")
                        startActivityForResult(intent, REQUEST_CODE_MANAGE_STORAGE)
                    } catch (e: Exception) {
                        val intent = Intent(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                        startActivityForResult(intent, REQUEST_CODE_MANAGE_STORAGE)
                    }
                } else {
                    Log.d("PermissionsDebug", "MANAGE_EXTERNAL_STORAGE permission already granted")
                }
            }
            else {
                Log.d("PermissionsDebug", "MANAGE_EXTERNAL_STORAGE permission already granted")
            }
        } else {
            // For Android versions below API 30, check traditional storage permissions
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                // Request permission for reading and writing external storage
                Log.d("PermissionsDebug", "READ/WRITE_EXTERNAL_STORAGE permission not granted.")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_MANAGE_STORAGE
                )
            } else {
                Log.d("PermissionsDebug", "READ/WRITE_EXTERNAL_STORAGE permission already granted")
            }
        }
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty()) {
            when (requestCode) {
                REQUEST_CODE_MANAGE_STORAGE -> {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("PermissionsDebug", "MANAGE_EXTERNAL_STORAGE permission granted")
                    } else {
                        Log.d("PermissionsDebug", "MANAGE_EXTERNAL_STORAGE permission denied")
                    }
                }
            }
        }
    }
}
