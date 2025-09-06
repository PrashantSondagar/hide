package com.example.hide

import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class HiddenFilesActivity : AppCompatActivity() {

    private lateinit var gridView: GridView
    private lateinit var galleryAdapter: GalleryAdapter
    private var hiddenItems: MutableList<String> = mutableListOf()
    private var hiddenDir: File? = null

    private val prefs by lazy { getSharedPreferences("HiddenPrefs", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hidden_files)

        gridView = findViewById(R.id.hiddenGridView)

        hiddenDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Hidden")
        if (!hiddenDir!!.exists()) hiddenDir!!.mkdirs()

        loadHiddenFiles()

        findViewById<Button>(R.id.unhideButton).setOnClickListener {
            val selected = galleryAdapter.getSelectedItems()
            if (selected.isNotEmpty()) {
                unhideFiles(selected)
            } else {
                Toast.makeText(this, "No items selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadHiddenFiles() {
        hiddenItems.clear()
        val files = hiddenDir?.listFiles()
        files?.forEach {
            hiddenItems.add(it.absolutePath)
        }

        galleryAdapter = GalleryAdapter(this, hiddenItems)
        gridView.adapter = galleryAdapter
    }

    private fun unhideFiles(items: List<String>) {
        for (path in items) {
            val file = File(path)
            if (file.exists()) {
                val originalPath = getOriginalPath(file.absolutePath)
                if (originalPath != null) {
                    val dest = File(originalPath)
                    dest.parentFile?.mkdirs()

                    try {
                        file.copyTo(dest, overwrite = true)
                        file.delete()
                        removeOriginalPath(file.absolutePath)

                        // Refresh in gallery instantly
                        MediaScannerConnection.scanFile(
                            this,
                            arrayOf(dest.absolutePath),
                            null
                        ) { _, _ ->
                            runOnUiThread {
                                Toast.makeText(
                                    this,
                                    "Unhidden: ${dest.name}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Original path not found for ${file.name}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        loadHiddenFiles()
    }

    // ---------- SharedPreferences mapping ----------
    private fun getOriginalPath(hiddenPath: String): String? {
        return prefs.getString(hiddenPath, null)
    }

    private fun removeOriginalPath(hiddenPath: String) {
        prefs.edit().remove(hiddenPath).apply()
    }
}
