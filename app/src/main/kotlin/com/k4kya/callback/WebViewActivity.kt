package com.k4kya.callback

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.webkit.WebView
import org.jetbrains.anko.findOptional

class WebViewActivity : AppCompatActivity() {

    val webView: WebView?
        get() = findOptional(R.id.web_view)

    val toolbar: Toolbar?
        get() = findOptional(R.id.toolbar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.webview_activity)
        val target = intent.extras.get("content") as String
        when (target) {
            "Open Source Licenses" -> {
                webView?.loadUrl("file:///android_asset/openSourceLicenses.html")
            }
        }
        setupToolbar(target)
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

    private fun setupToolbar(target: String) {
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar?.title = target
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

}