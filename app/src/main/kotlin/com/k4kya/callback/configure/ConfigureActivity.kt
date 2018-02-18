package com.k4kya.callback.configure

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatSpinner
import android.support.v7.widget.SwitchCompat
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.content.edit
import com.k4kya.callback.R
import com.k4kya.callback.about.AboutActivity
import com.k4kya.callback.interactor.ConfigureInteractorImpl
import com.k4kya.callback.interactor.SmsListener
import com.k4kya.callback.onboarding.OnboardingActivity
import com.k4kya.callback.util.bindView
import com.k4kya.callback.util.intentFor

class ConfigureActivity : AppCompatActivity(), ConfigureView {
    
    private lateinit var presenter: ConfigurePresenter
    
    private val requiredPermissions = listOf(
            Manifest.permission.CALL_PHONE,
            Manifest.permission.RECEIVE_SMS
    )
    private val actionbar: Toolbar by bindView(R.id.toolbar)
    private val btnToggle: SwitchCompat by bindView(R.id.btnToggleService)
    private val editTriggerPhrase: EditText by bindView(R.id.editTriggerPhrase)
    private val checkUseSpeaker: CheckBox by bindView(R.id.checkUseSpeaker)
    private val spinnerPhoneApp: AppCompatSpinner by bindView(R.id.spinner_calling_app)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showOnboardingIfNecessary()
        checkPermissions()
        setContentView(R.layout.activity_main)
        setSupportActionBar(actionbar)
        presenter = ConfigurePresenterImpl(ConfigureInteractorImpl(applicationContext), this)
        setupUI()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_about -> {
                startActivity(intentFor<AboutActivity>())
                true
            }
            else -> false
        }
    }
    
    override fun update(viewModel: ConfigureViewModel) {
        checkUseSpeaker.isChecked = viewModel.speakerEnabled
        editTriggerPhrase.setText(viewModel.triggerPhrase)
        btnToggle.isChecked = viewModel.toggleEnabled
        btnToggle.text = getString(when (viewModel.toggleEnabled) {
            true -> R.string.callback_service_started
            false -> R.string.callback_service_stopped
        })
    }
    
    private fun showOnboardingIfNecessary() {
        val shown = getSharedPrefs().getBoolean(getString(R.string.intro_shown), false)
        if (!shown) {
            startActivity(intentFor<OnboardingActivity>())
            getSharedPrefs().edit { putBoolean(getString(R.string.intro_shown), true) }
        }
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
        btnToggle.setOnCheckedChangeListener { _, checked -> presenter.setCallbackEnabled(checked, editTriggerPhrase.text.toString()) }
        checkUseSpeaker.setOnCheckedChangeListener { _, checked -> presenter.setStartOnSpeaker(checked) }
        setupPhoneApps()
    }
    
    private fun setupPhoneApps() {
        val dialIntent = Intent(Intent.ACTION_CALL)
        dialIntent.data = Uri.parse("tel:123")
        dialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val availableApps = packageManager.queryIntentActivities(dialIntent, PackageManager.GET_META_DATA.or(PackageManager.GET_ACTIVITIES))
        val apps = availableApps.map {
            CallingApp(it.loadIcon(packageManager), it.loadLabel(packageManager).toString())
        }
        val arrayAdapter = CallingAppAdapter(apps)
        spinnerPhoneApp.adapter = arrayAdapter
        
    }
    
    private fun getSharedPrefs(): SharedPreferences {
        return applicationContext.getSharedPreferences(SmsListener.CALLBACK_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissions.forEach {
            val index = permissions.indexOf(it)
            val granted = grantResults[index]
            if (granted != PackageManager.PERMISSION_GRANTED) {
                presenter.setCallbackEnabled(false, "")
            }
        }
    }
    
    override fun showMessage(resId: Int) {
        showMessage(getString(resId))
    }
    
    override fun showMessage(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
    }
}
