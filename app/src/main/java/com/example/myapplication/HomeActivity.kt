package com.example.myapplication

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar = findViewById<Toolbar>(R.id.homeToolbar)
        setSupportActionBar(toolbar)
        // The toolbar contains a custom drawable-based YummCart brand lockup.
        supportActionBar?.title = ""

        val name = intent.getStringExtra("user_name")
            ?: getString(R.string.p4_default_user)
        findViewById<TextView>(R.id.tvWelcome).text =
            getString(R.string.p4_welcome_user, name)

        findViewById<View>(R.id.navHome).setOnClickListener {
            showMainFragment(HomeFragment())
            selectNavigation(R.id.navHome)
        }
        findViewById<View>(R.id.navCategories).setOnClickListener {
            showMainFragment(CategoriesFragment())
            selectNavigation(R.id.navCategories)
        }
        findViewById<View>(R.id.navAccount).setOnClickListener {
            showMainFragment(AccountFragment())
            selectNavigation(R.id.navAccount)
        }
        findViewById<View>(R.id.navCart).setOnClickListener {
            showMainFragment(CartFragment())
            selectNavigation(R.id.navCart)
        }

        if (savedInstanceState == null) {
            showMainFragment(HomeFragment())
            selectNavigation(R.id.navHome)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_profile -> {
                showMainFragment(AccountFragment())
                selectNavigation(R.id.navAccount)
                true
            }
            R.id.menu_about -> {
                AlertDialog.Builder(this)
                    .setTitle(R.string.p4_about_title)
                    .setMessage(R.string.p4_about_message)
                    .setPositiveButton(R.string.p4_ok, null)
                    .show()
                true
            }
            R.id.menu_settings -> {
                AlertDialog.Builder(this)
                    .setTitle(R.string.p4_settings_title)
                    .setMessage(R.string.p4_settings_message)
                    .setPositiveButton(R.string.p4_ok, null)
                    .show()
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

    private fun selectNavigation(selectedId: Int) {
        val navItems = listOf(
            Triple(R.id.navHome, R.id.navHomeIcon, R.id.navHomeLabel),
            Triple(R.id.navCategories, R.id.navCategoriesIcon, R.id.navCategoriesLabel),
            Triple(R.id.navAccount, R.id.navAccountIcon, R.id.navAccountLabel),
            Triple(R.id.navCart, R.id.navCartIcon, R.id.navCartLabel)
        )

        navItems.forEach { (containerId, iconId, labelId) ->
            val selected = containerId == selectedId
            val color = ContextCompat.getColor(
                this,
                if (selected) R.color.primary_blue else R.color.secondary_text
            )
            findViewById<ImageView>(iconId).setColorFilter(color)
            findViewById<TextView>(labelId).setTextColor(color)
            findViewById<View>(containerId).isSelected = selected
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.p4_logout_title)
            .setMessage(R.string.p4_logout_confirmation)
            .setPositiveButton(R.string.p4_yes) { _, _ -> finishAffinity() }
            .setNegativeButton(R.string.p4_no, null)
            .show()
    }
}
