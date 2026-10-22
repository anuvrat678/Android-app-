package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignupActivity : AppCompatActivity() {

    private lateinit var db: AppDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        db = AppDatabaseHelper(this)

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val cbAgree = findViewById<CheckBox>(R.id.cbAgree)

        val btnCreateAccount = findViewById<Button>(R.id.btnCreateAccount)
        val tvBack = findViewById<TextView>(R.id.tvBack)
        val tvGoToSignIn = findViewById<TextView>(R.id.tvGoToSignIn)

        btnCreateAccount.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!cbAgree.isChecked) {
                Toast.makeText(this, "Please agree to processing of Personal data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newUser = User(
                id = 0,
                name = name,
                email = email,
                password = password
            )

            val success = db.insertUser(newUser)
            if (success) {
                Toast.makeText(this, "Account created successfully in SQLite database!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SigninActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Failed to create account. Email may already be registered.", Toast.LENGTH_SHORT).show()
            }
        }

        tvBack.setOnClickListener {
            finish()
        }

        tvGoToSignIn.setOnClickListener {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
