package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

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

        val btnFingerprint =
            findViewById<Button>(R.id.btnFingerprint)

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
                intent.putExtra("user_name", "Mahendra Rajpurohit")
                startActivity(intent)

            } else {

                Toast.makeText(
                    this,
                    "Invalid email or password",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Fingerprint Authentication button click listener
        btnFingerprint.setOnClickListener {
            showBiometricPrompt()
        }
    }

    private fun showBiometricPrompt() {
        val biometricManager = BiometricManager.from(this)

        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                val executor = ContextCompat.getMainExecutor(this)
                val biometricPrompt = BiometricPrompt(
                    this,
                    executor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            Toast.makeText(
                                this@SigninActivity,
                                "Fingerprint Authentication Successful!",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(this@SigninActivity, HomeActivity::class.java)
                            intent.putExtra("user_name", "Mahendra Rajpurohit")
                            startActivity(intent)
                            finish()
                        }

                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            super.onAuthenticationError(errorCode, errString)
                            Toast.makeText(
                                this@SigninActivity,
                                "Authentication error: $errString",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onAuthenticationFailed() {
                            super.onAuthenticationFailed()
                            Toast.makeText(
                                this@SigninActivity,
                                "Authentication failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )

                val promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.biometric_title))
                    .setSubtitle(getString(R.string.biometric_subtitle))
                    .setNegativeButtonText(getString(R.string.cancel))
                    .build()

                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(
                    this,
                    "No biometric features available on this device.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(
                    this,
                    "Biometric features are currently unavailable.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(
                    this,
                    "No fingerprint enrolled. Please setup fingerprint in device settings.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                Toast.makeText(
                    this,
                    "Biometric authentication not supported.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}