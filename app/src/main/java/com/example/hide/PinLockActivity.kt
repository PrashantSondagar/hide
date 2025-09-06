package com.example.hide

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PinLockActivity : AppCompatActivity() {

    private val correctPin = "1234" // This should be saved securely in a real app
    private lateinit var pinInput: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_lock)

        pinInput = findViewById(R.id.pinInput)
        submitButton = findViewById(R.id.submitButton)

        submitButton.setOnClickListener {
            val enteredPin = pinInput.text.toString()

            if (enteredPin == correctPin) {
                // PIN is correct, allow access to unhide
                val intent = Intent(this, GalleryActivity::class.java)
                startActivity(intent)
                finish() // Close PinLockActivity
            } else {
                // PIN is incorrect
                Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
