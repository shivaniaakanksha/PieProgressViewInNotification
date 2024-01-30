package com.example.pieprogressviewinnotification

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button: Button = findViewById<Button>(R.id.button_show_notification)
        button.setOnClickListener {
            startBGService()
        }
    }

    private fun startBGService() {
        Intent(this, BGService::class.java).apply {
            startService(this)
        }
    }
}