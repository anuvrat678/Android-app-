package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

/** Practical 5A splash screen. Continues to the existing welcome/authentication flow. */
class SplashActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())
    private val openWelcome = Runnable {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        handler.postDelayed(openWelcome, SPLASH_DURATION_MS)
    }

    override fun onDestroy() {
        handler.removeCallbacks(openWelcome)
        super.onDestroy()
    }

    companion object {
        private const val SPLASH_DURATION_MS = 1800L
    }
}
