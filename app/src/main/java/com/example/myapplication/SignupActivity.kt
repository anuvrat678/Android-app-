package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val btnCreateAccount =
            findViewById<Button>(R.id.btnCreateAccount)

        val tvBack =
            findViewById<TextView>(R.id.tvBack)

        val tvGoToSignIn =
            findViewById<TextView>(R.id.tvGoToSignIn)

        // Sign Up button opens Sign In screen
        btnCreateAccount.setOnClickListener {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
        }

        // Back button
        tvBack.setOnClickListener {
            finish()
        }

        // Existing account link
        tvGoToSignIn.setOnClickListener {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
        }
    }
}