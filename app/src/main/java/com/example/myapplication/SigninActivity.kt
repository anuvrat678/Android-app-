package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SigninActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail =
            findViewById<EditText>(R.id.etLoginEmail)

        val etPassword =
            findViewById<EditText>(R.id.etLoginPassword)

        val btnLogin =
            findViewById<Button>(R.id.btnLogin)

        val tvBack =
            findViewById<TextView>(R.id.tvBack)

        val tvGoToSignUp =
            findViewById<TextView>(R.id.tvGoToSignUp)

        val tvForgotPassword =
            findViewById<TextView>(R.id.tvForgotPassword)

        // Static login credentials
        val correctEmail = "mahendra@gmail.com"
        val correctPassword = "12345678"

        // Back button
        tvBack.setOnClickListener {
            finish()
        }

        // Open Sign Up screen
        tvGoToSignUp.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        // Forgot password message
        tvForgotPassword.setOnClickListener {
            Toast.makeText(
                this,
                "Please contact the administrator",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Verify login credentials
        btnLogin.setOnClickListener {

            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {

                Toast.makeText(
                    this,
                    "Enter email and password",
                    Toast.LENGTH_SHORT
                ).show()

            } else if (
                email.equals(correctEmail, ignoreCase = true) &&
                password == correctPassword
            ) {

                Toast.makeText(
                    this,
                    "Login successful",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("user_name", "Kristin Watson")
                startActivity(intent)

            } else {

                Toast.makeText(
                    this,
                    "Invalid email or password",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}