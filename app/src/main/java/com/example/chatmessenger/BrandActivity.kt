package com.example.chatmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class BrandActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_brand)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this,OnBoardingActivity::class.java))
        },3000)
    }
}