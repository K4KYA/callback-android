package com.k4kya.callback

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import org.jetbrains.anko.find
import org.jetbrains.anko.findOptional
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {
    val requiredPermissions = listOf(
            Manifest.permission.CALL_PHONE,
            Manifest.permission.RECEIVE_SMS
    )

    val TRIGGER_PHRASE_MIN_LENGTH = 4

    val actionbar: Toolbar?
        get() = findOptional(R.id.toolbar)

    val btnToggle: Button?
        get() = findOptional(R.id.btnToggleService)

    val editTriggerPhrase: EditText?
        get() = findOptional(R.id.editTriggerPhrase)

    val btnSetTriggerPhrase: Button?
        get() = findOptional(R.id.btnSetTriggerPhrase)

    val checkUseSpeaker: CheckBox?
        get() = findOptional(R.id.checkUseSpeaker)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showOnboardingIfNecessary();
        checkPermissions()
        setContentView(R.layout.activity_main)
        setSupportActionBar(actionbar)
        setupUI()
    }

    fun showOnboardingIfNecessary() {
        val shown = getSharedPrefs(this)?.getBoolean(getString(R.string.intro_shown), false) ?: false
        if (!shown) {
            startActivity<OnboardingActivity>()
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
        startActivity<AboutActivity>()
    }

    private fun checkPermissions() {
        var permissionsRequestedFlags = 2
        var permissionsToRequest = emptyList<String>()

        requiredPermissions.forEach {
            if (!hasPermission(it)) {
                permissionsToRequest = permissionsToRequest.plus(it)
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
        setupTriggerPhraseUpdateButton()
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

    private fun setupTriggerPhraseUpdateButton() {
        btnSetTriggerPhrase?.setOnClickListener {
            updateTriggerPhrase()
        }
    }

    private fun setupTriggerPhraseInput() {
        editTriggerPhrase?.setText(loadTriggerPhrase() ?: "")
    }

    private fun setupServiceToggleButton() {
        val btnToggle = btnToggle
        btnToggle?.setOnClickListener { setCallbackServiceEnabled(!getCallbackServiceEnabledStatus()) }
    }

    private fun updateServiceStatusText() {
        val serviceEnabled = getCallbackServiceEnabledStatus()
        btnToggle?.text = getString(if (serviceEnabled) R.string.disable_callback else R.string.enable_callback)
    }

    private fun saveTriggerPhrase(triggerPhrase: String): () -> Boolean {
        getSharedPrefs(this)
                ?.edit()
                ?.putString(SmsListener.Constants.CALLBACK_SERVICE_TRIGGER_PHRASE, triggerPhrase)
                ?.apply()
                ?: return { showMessage(R.string.trigger_not_updated); false }
        return { showMessage(R.string.trigger_updated); true }
    }

    private fun getTriggerPhrase(): String? {
        val existingTrigger = loadTriggerPhrase()
        val currentOnScreenTrigger = editTriggerPhrase?.text?.toString()
        return (if (existingTrigger != currentOnScreenTrigger) currentOnScreenTrigger else existingTrigger)
    }

    private fun loadTriggerPhrase(): String? = getSharedPrefs(this)?.getString(SmsListener.Constants.CALLBACK_SERVICE_TRIGGER_PHRASE, "")

    private fun getCallbackServiceEnabledStatus(): Boolean {
        return getSharedPrefs(this)?.getBoolean(SmsListener.Constants.CALLBACK_SERVICE_ENABLED_FLAG, false) ?: false
    }

    private fun validateTriggerPhraseLength(triggerPhrase: String): Boolean {
        if (triggerPhrase.length >= TRIGGER_PHRASE_MIN_LENGTH) {
            return true
        }
        return false
    }

    private fun setCallbackServiceEnabled(enabled: Boolean) {
        if (updateTriggerPhrase()) {
            val sharedPrefsEditor = getSharedPrefs(this)?.edit() ?: return
            sharedPrefsEditor.putBoolean(SmsListener.Constants.CALLBACK_SERVICE_ENABLED_FLAG, enabled).apply()
            updateServiceStatusText()
            showMessage(if (enabled) R.string.callback_enabled else R.string.callback_disabled)
        }
    }

    private fun updateTriggerPhrase(): Boolean {
        val triggerPhrase = getTriggerPhrase()
        if (triggerPhrase != null) {
            if (!validateTriggerPhraseLength(triggerPhrase)) {
                showMessage(R.string.error_trigger_min_length)
                return false
            }
            return saveTriggerPhrase(triggerPhrase)()
        }
        return false
    }

    private fun getSharedPrefs(context: Context?): SharedPreferences? {
        return context?.getSharedPreferences(SmsListener.Constants.CALLBACK_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissions.forEach {
            val index = permissions.indexOf(it)
            val granted = grantResults[index]
            if (granted != PackageManager.PERMISSION_GRANTED) {
                setCallbackServiceEnabled(false)
            }
        }
    }

    private fun showMessage(resId: Int) {
        Snackbar.make(find(android.R.id.content), resId, Snackbar.LENGTH_LONG).show()
    }
}
