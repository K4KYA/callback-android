package com.k4kya.callback

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    val requestingSmsPermission = 1.shl(0)
    val requestingPhonePermission = 1.shl(1)
    val requiredPermissions = listOf(
            RequestedPermissions(Manifest.permission.CALL_PHONE, requestingPhonePermission),
            RequestedPermissions(Manifest.permission.RECEIVE_SMS, requestingSmsPermission)
    )

    val toolbar: Toolbar?
        get() {
            return findViewById(R.id.toolbar) as Toolbar?
        }

    val btnToggle: Button?
        get() {
            return findViewById(R.id.btnToggleService) as Button?
        }

    val editTriggerPhrase: EditText?
        get() {
            return findViewById(R.id.editTriggerPhrase) as EditText?
        }

    val btnSetTriggerPhrase: Button?
        get() {
            return findViewById(R.id.btnSetTriggerPhrase) as Button?
        }

    val checkUseSpeaker: CheckBox?
        get() {
            return findViewById(R.id.checkUseSpeaker) as CheckBox?
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showOnboardingIfNecessary();
        checkPermissions()
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setupUI()
    }

    fun showOnboardingIfNecessary() {
        val shown = getSharedPrefs(this)?.getBoolean(getString(R.string.intro_shown), false) ?: false
        if (!shown) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            getSharedPrefs(this)?.edit()?.putBoolean(getString(R.string.intro_shown), true)?.apply()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            when (item.itemId) {
                R.id.menu_about -> {
                    startAboutActivity(); return true
                }
                else -> return false
            }
        }
        return false
    }

    private fun startAboutActivity() {
        val aboutIntent = Intent(this, AboutActivity::class.java)
        startActivity(aboutIntent)
    }

    private fun checkPermissions() {
        var permissionsRequestedFlags = 0
        var permissionsToRequest = emptyList<String>()

        requiredPermissions.map {
            if (!hasPermission(it.name)) {
                permissionsToRequest = permissionsToRequest.plus(it.name)
                permissionsRequestedFlags.or(it.code)
            }
        }

        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), permissionsRequestedFlags)
        }
    }

    private fun hasPermission(permission: String): Boolean {
        var status = ContextCompat.checkSelfPermission(this, permission)
        return (status == PackageManager.PERMISSION_GRANTED)
    }

    private fun setupUI() {
        setupServiceToggleButton()
        setupTriggerPhraseInput()
        setupTriggerPhraseButton()
        setupUseSpeaker()
        updateServiceStatusText()
    }

    private fun setupUseSpeaker() {
        checkUseSpeaker?.isChecked = getSharedPrefs(this)?.getBoolean(SmsListener.Constants.CALLBACK_USE_SPEAKERPHONE, false) ?: false
        checkUseSpeaker?.setOnCheckedChangeListener {
            view, checked ->
            getSharedPrefs(this)
                    ?.edit()
                    ?.putBoolean(SmsListener.Constants.CALLBACK_USE_SPEAKERPHONE, checked)
                    ?.apply()
        }
    }

    private fun setupTriggerPhraseButton() {
        btnSetTriggerPhrase?.setOnClickListener { setTriggerPhrase() }
    }

    private fun setupTriggerPhraseInput() {
        editTriggerPhrase?.setText(getTriggerPhrase() ?: "")
    }

    private fun setupServiceToggleButton() {
        val btnToggle = btnToggle
        btnToggle?.setOnClickListener { setCallbackServiceEnabled(!getCallbackServiceEnabledStatus()) }
    }

    private fun updateServiceStatusText() {
        val serviceEnabled = getCallbackServiceEnabledStatus()
        btnToggle?.text = getString(if (serviceEnabled) R.string.disable_callback else R.string.enable_callback)
    }

    private fun setTriggerPhrase() {
        val triggerPhrase = editTriggerPhrase?.text?.toString() ?: return
        if (triggerPhrase.length < 4) {
            Toast.makeText(this, R.string.error_trigger_min_length, Toast.LENGTH_LONG).show()
        }
        getSharedPrefs(this)
                ?.edit()
                ?.putString(SmsListener.Constants.CALLBACK_SERVICE_TRIGGER_PHRASE, triggerPhrase)
                ?.apply()
                ?: return
        showMessage(R.string.trigger_updated)
    }

    private fun getTriggerPhrase(): String? {
        return getSharedPrefs(this)?.getString(SmsListener.Constants.CALLBACK_SERVICE_TRIGGER_PHRASE, "")
    }

    private fun getCallbackServiceEnabledStatus(): Boolean {
        return getSharedPrefs(this)?.getBoolean(SmsListener.Constants.CALLBACK_SERVICE_ENABLED_FLAG, false) ?: false
    }

    private fun validateTriggerPhrase(): Boolean {
        val triggerPhrase = getTriggerPhrase()
        if (triggerPhrase != null) {
            if (triggerPhrase.length >= 4) {
                return true
            }
        }
        showMessage(R.string.error_trigger_min_length)
        return false
    }

    private fun setCallbackServiceEnabled(enabled: Boolean) {
        if (!validateTriggerPhrase()) {
            return
        }
        val sharedPrefsEditor = getSharedPrefs(this)?.edit() ?: return
        sharedPrefsEditor.putBoolean(SmsListener.Constants.CALLBACK_SERVICE_ENABLED_FLAG, enabled).apply()
        updateServiceStatusText()
        showMessage(if (enabled) R.string.callback_enabled else R.string.callback_disabled)
    }

    private fun getSharedPrefs(context: Context?): SharedPreferences? {
        return context?.getSharedPreferences(SmsListener.Constants.CALLBACK_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissions.map {
            val index = permissions.indexOf(it)
            val granted = grantResults[index]
            if (granted != PackageManager.PERMISSION_GRANTED) {
                setCallbackServiceEnabled(false)
            }
        }
    }

    private fun showMessage(resId: Int) {
        Toast.makeText(this, resId, Toast.LENGTH_LONG).show()
    }

    data class RequestedPermissions(val name: String, val code: Int)
}
