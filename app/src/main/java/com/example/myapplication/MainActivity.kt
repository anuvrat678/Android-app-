package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSignIn = findViewById<TextView>(R.id.btnSignIn)
        val btnSignUp = findViewById<TextView>(R.id.btnSignUp)

        // Open Sign In screen
        btnSignIn.setOnClickListener {
            val intent = Intent(
                this,
                SigninActivity::class.java
            )
            startActivity(intent)
        }

        // Open Sign Up screen
        btnSignUp.setOnClickListener {
            val intent = Intent(
                this,
                SignupActivity::class.java
            )
            startActivity(intent)
        }
    }
}
