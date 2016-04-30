package com.k4kya.callback

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.TextView
import org.jetbrains.anko.browse
import org.jetbrains.anko.findOptional
import org.jetbrains.anko.onClick

class AboutActivity : AppCompatActivity() {

    private val toolbar: Toolbar?
        get() = findOptional(R.id.toolbar)

    val twitter: TextView?
        get() = findOptional(R.id.shamelessSelfPromo)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setupToolbar()
        fragmentManager.beginTransaction().replace(R.id.contentPanel, SettingsFragment()).commit()
        twitter?.onClick {
            browse("http://www.twitter.com/k4kya")
        }
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
