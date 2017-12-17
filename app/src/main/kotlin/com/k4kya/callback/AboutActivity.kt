package com.k4kya.callback

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.TextView
import com.k4kya.callback.util.bindView

class AboutActivity : AppCompatActivity() {

    private val toolbar: Toolbar by bindView(R.id.toolbar)

    private val twitter: TextView by bindView(R.id.shamelessSelfPromo)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setupToolbar()
        fragmentManager.beginTransaction().replace(R.id.contentPanel, SettingsFragment()).commit()
        twitter.setOnClickListener {
            val twitterIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("http://www.twitter.com/k4kya")
            }
            startActivity(twitterIntent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            return when (item.itemId) {
                android.R.id.home -> {
                    finish(); true
                }
                else -> false
            }
        }
        return false
    }

    private fun setupToolbar() {
            setSupportActionBar(toolbar)
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
