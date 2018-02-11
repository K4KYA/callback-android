package com.k4kya.callback.configure

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
import com.k4kya.callback.onboarding.OnboardingActivity
import com.k4kya.callback.service.PhoneCallbackService
import com.k4kya.callback.R
import com.k4kya.callback.service.SmsListener
import com.k4kya.callback.about.AboutActivity
import com.k4kya.callback.util.bindView
import com.k4kya.callback.util.intentFor
import com.k4kya.kotlinrxbindings.widgets.AfterTextChangedEvent
import com.k4kya.kotlinrxbindings.widgets.textEvents
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers

class ConfigureActivity : AppCompatActivity(), ConfigureView {
    
    private var presenter: ConfigurePresenter? = null
    
    private val requiredPermissions = listOf(
            Manifest.permission.CALL_PHONE,
            Manifest.permission.RECEIVE_SMS
    )
    private val actionbar: Toolbar by bindView(R.id.toolbar)
    private val btnToggle: Button by bindView(R.id.btnToggleService)
    private val editTriggerPhrase: EditText by bindView(R.id.editTriggerPhrase)
    private val btnSetTriggerPhrase: Button by bindView(R.id.btnSetTriggerPhrase)
    private val checkUseSpeaker: CheckBox by bindView(R.id.checkUseSpeaker)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showOnboardingIfNecessary();
        checkPermissions()
        setContentView(R.layout.activity_main)
        setSupportActionBar(actionbar)
        presenter = ConfigurePresenterImpl(PhoneCallbackService(this), this)
        setupUI()
    }


    override fun onResume() {
        super.onResume()
        setupTextWatcher()
    }

    override fun onPause() {
        super.onPause()
        textWatcher?.unsubscribe()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_about -> {
                startAboutActivity(); true
            }
            else -> false
        }
    }

    private fun showOnboardingIfNecessary() {
        val shown = getSharedPrefs(this)?.getBoolean(getString(R.string.intro_shown), false) ?: false
        if (!shown) {
            startActivity(intentFor<OnboardingActivity>())
            getSharedPrefs(this)?.edit()?.putBoolean(getString(R.string.intro_shown), true)?.apply()
        }
    }

    private fun startAboutActivity() {
        startActivity(intentFor<AboutActivity>())
    }

    private fun checkPermissions() {
        val permissionsRequestedFlags = 2
        var permissionsToRequest = emptyList<String>()

        requiredPermissions.forEach {
            if (!hasPermission(it)) {
                permissionsToRequest = permissionsToRequest.plus(it)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), permissionsRequestedFlags)
        }
    }

    private fun hasPermission(permission: String): Boolean {
        val status = ContextCompat.checkSelfPermission(this, permission)
        return (status == PackageManager.PERMISSION_GRANTED)
    }

    private fun setupUI() {
        setupServiceToggleButton()
        setupTriggerPhraseUpdateButton()
        setupUseSpeaker()
    }

    private var textWatcher: Subscription? = null

    private fun setupTextWatcher() {
        textWatcher = editTriggerPhrase.textEvents()
                .filter { it is AfterTextChangedEvent }
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        is AfterTextChangedEvent -> {
                            if (it.value != null) {
                                presenter?.setTriggerPhrase(it.value.toString())
                            }
                        }
                    }
                }
    }

    private fun setupUseSpeaker() {
        checkUseSpeaker.setOnCheckedChangeListener { _, checked ->
            presenter?.setStartOnSpeaker(checked)
        }
    }

    private fun setupTriggerPhraseUpdateButton() {
        btnSetTriggerPhrase.setOnClickListener {
            presenter?.setTriggerPhrase(editTriggerPhrase.text.toString())
        }
    }

    private fun setupServiceToggleButton() {
        val btnToggle = btnToggle
        btnToggle.setOnClickListener { presenter?.toggleCallbackEnabled() }
    }

    private fun getSharedPrefs(context: Context?): SharedPreferences? {
        return context?.getSharedPreferences(SmsListener.CALLBACK_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
    }

    override fun updateStatusText(enabled: Boolean) {
        btnToggle.text = getString(if (enabled) R.string.stop_callback_service else R.string.start_callback_service)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissions.forEach {
            val index = permissions.indexOf(it)
            val granted = grantResults[index]
            if (granted != PackageManager.PERMISSION_GRANTED) {
                presenter?.setCallbackEnabled(false)
            }
        }
    }

    override fun showMessage(resId: Int) {
        showMessage(getString(resId))
    }

    override fun showMessage(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
    }

    override fun setCallbackStatusText(status: String) {
        btnToggle.text = status
    }

    override fun setTriggerPhrase(trigger: CharSequence?) {
        editTriggerPhrase.setText(trigger!!)
    }

    override fun setSpeakerEnabled(enabled: Boolean) {
        checkUseSpeaker.isChecked = enabled
    }

    override fun setServiceToggleButtonEnabled(enabled: Boolean) {
        btnToggle.isEnabled = enabled
    }

    override fun getLatestTriggerPhrase() = editTriggerPhrase.text?.toString()
}
