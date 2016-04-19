package com.k4kya.callback

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem

class AboutActivity : AppCompatActivity() {

    private val toolbar: Toolbar?
        get() {
            val toolbar = findViewById(R.id.toolbar) as Toolbar?
            return toolbar
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setupToolbar()
        fragmentManager.beginTransaction().replace(R.id.contentPanel, SettingsFragment()).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            when (item.itemId) {
                android.R.id.home -> {
                    finish(); return true
                }
                else -> return false
            }
        }
        return false
    }

    private fun setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }
}
