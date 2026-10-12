
package com.example.myapplication

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar = findViewById<Toolbar>(R.id.homeToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "YummCart"

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnCategories = findViewById<Button>(R.id.btnShowCategories)
        val btnCart = findViewById<Button>(R.id.btnShowCart)

        val userName = intent.getStringExtra("user_name") ?: "User"
        tvWelcome.text = "Welcome, $userName!"

        btnCategories.setOnClickListener {
            showMainFragment(CategoriesFragment())
        }

        btnCart.setOnClickListener {
            showMainFragment(CartFragment())
        }

        btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        if (savedInstanceState == null) {
            showMainFragment(CategoriesFragment())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_profile -> {
                showMessage("User Profile selected")
                true
            }

            R.id.menu_about -> {
                showMessage("YummCart Food Ordering App")
                true
            }

            R.id.menu_settings -> {
                showMessage("Settings selected")
                true
            }

            R.id.menu_logout -> {
                showLogoutDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showMainFragment(fragment: Fragment) {
        supportFragmentManager.popBackStackImmediate(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                finishAffinity()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
