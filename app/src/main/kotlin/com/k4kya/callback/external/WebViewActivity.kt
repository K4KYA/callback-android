package com.k4kya.callback.external

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.webkit.WebView
import com.k4kya.callback.R
import com.k4kya.callback.util.bindView

class WebViewActivity : AppCompatActivity() {

    val webView: WebView by bindView(R.id.web_view)

    val toolbar: Toolbar by bindView(R.id.toolbar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.webview_activity)
        val target = intent.extras.get("content") as String
        when (target) {
            "Open Source Licenses" -> {
                webView.loadUrl("file:///android_asset/openSourceLicenses.html")
            }
        }
        setupToolbar(target)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish(); true
            }
            else -> false
        }
    }

    private fun setupToolbar(target: String) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = target
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}