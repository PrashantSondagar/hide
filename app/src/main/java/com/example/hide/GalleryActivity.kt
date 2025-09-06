package com.example.hide

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File

class GalleryActivity : AppCompatActivity() {

    private lateinit var gridView: GridView
    private lateinit var galleryAdapter: GalleryAdapter
    private var allItems: MutableList<String> = mutableListOf()
    private var hiddenDir: File? = null

    private val prefs by lazy { getSharedPreferences("HiddenPrefs", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        gridView = findViewById(R.id.galleryGridView)

        findViewById<Button>(R.id.openHiddenButton).setOnClickListener {
            val intent = Intent(this, HiddenFilesActivity::class.java)
            startActivity(intent)
        }

        hiddenDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Hidden")
        if (!hiddenDir!!.exists()) hiddenDir!!.mkdirs()

        // Permission check
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    101
                )
            } else {
                loadGallery()
            }
        } else {
            loadGallery()
        }

        findViewById<Button>(R.id.hideButton).setOnClickListener {
            val selected = galleryAdapter.getSelectedItems()
            if (selected.isNotEmpty()) {
                hideFiles(selected)
            } else {
                Toast.makeText(this, "No items selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadGallery() {
        allItems.clear()
        val uriExternal: Uri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.MEDIA_TYPE
        )
        val selection =
            ("${MediaStore.Files.FileColumns.MEDIA_TYPE}=${MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE}" +
                    " OR " +
                    "${MediaStore.Files.FileColumns.MEDIA_TYPE}=${MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO}")

        val cursor = contentResolver.query(uriExternal, projection, selection, null, null)
        cursor?.use {
            val columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            while (cursor.moveToNext()) {
                val filePath = cursor.getString(columnIndexData)
                allItems.add(filePath)
            }
        }

        galleryAdapter = GalleryAdapter(this, allItems)
        gridView.adapter = galleryAdapter
    }

    private fun hideFiles(items: List<String>) {
        for (path in items) {
            val file = File(path)
            if (file.exists()) {
                val dest = File(hiddenDir, file.name)

                // Save mapping hiddenPath -> originalPath
                saveOriginalPath(dest.absolutePath, file.absolutePath)

                try {
                    file.copyTo(dest, overwrite = true)
                    file.delete()

                    // Remove from gallery instantly
                    MediaScannerConnection.scanFile(
                        this,
                        arrayOf(file.absolutePath),
                        null
                    ) { _, _ -> }

                    Toast.makeText(this, "Hidden: ${file.name}", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        loadGallery()
    }

    // ---------- SharedPreferences mapping ----------
    private fun saveOriginalPath(hiddenPath: String, originalPath: String) {
        prefs.edit().putString(hiddenPath, originalPath).apply()
    }
}
