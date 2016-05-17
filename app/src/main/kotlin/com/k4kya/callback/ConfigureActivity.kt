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
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import org.jetbrains.anko.find
import org.jetbrains.anko.findOptional
import org.jetbrains.anko.startActivity
import rx.Observable
import rx.android.MainThreadSubscription
import java.util.concurrent.TimeUnit

class ConfigureActivity : AppCompatActivity(), ConfigureMvp.View {

    val requiredPermissions = listOf(
            Manifest.permission.CALL_PHONE,
            Manifest.permission.RECEIVE_SMS
    )
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

    val presenter: ConfigureMvp.Presenter
        get() = ConfigurePresenter(PhoneCallbackService(this), this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showOnboardingIfNecessary();
        checkPermissions()
        setContentView(R.layout.activity_main)
        setSupportActionBar(actionbar)
        setupUI()
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

    fun showOnboardingIfNecessary() {
        val shown = getSharedPrefs(this)?.getBoolean(getString(R.string.intro_shown), false) ?: false
        if (!shown) {
            startActivity<OnboardingActivity>()
            getSharedPrefs(this)?.edit()?.putBoolean(getString(R.string.intro_shown), true)?.apply()
        }
    }

    fun startAboutActivity() {
        startActivity<AboutActivity>()
    }

    fun checkPermissions() {
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

    fun hasPermission(permission: String): Boolean {
        var status = ContextCompat.checkSelfPermission(this, permission)
        return (status == PackageManager.PERMISSION_GRANTED)
    }

    fun setupUI() {
        setupServiceToggleButton()
        setupTriggerPhraseUpdateButton()
        setupUseSpeaker()
        setupTextWatcher()
    }

    private fun setupTextWatcher() {
        editTriggerPhrase?.addTextChangedListener()
                ?.filter { it.eventType == TextWatcherEvent.afterChange }
                ?.debounce(1, TimeUnit.SECONDS)
                ?.subscribe { Log.d("TextWatcherSubscriber: ", "" + it.value) }
    }

    fun setupUseSpeaker() {
        checkUseSpeaker?.setOnCheckedChangeListener {
            view, checked ->
            presenter.setStartOnSpeaker(checked)
        }
    }

    fun setupTriggerPhraseUpdateButton() {
        btnSetTriggerPhrase?.setOnClickListener {
            presenter.setTriggerPhrase(editTriggerPhrase?.text.toString())
        }
    }

    fun setupServiceToggleButton() {
        val btnToggle = btnToggle
        btnToggle?.setOnClickListener { presenter.toggleCallbackEnabled() }
    }

    fun getSharedPrefs(context: Context?): SharedPreferences? {
        return context?.getSharedPreferences(SmsListener.CALLBACK_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissions.forEach {
            val index = permissions.indexOf(it)
            val granted = grantResults[index]
            if (granted != PackageManager.PERMISSION_GRANTED) {
                presenter.setCallbackEnabled(false)
            }
        }
    }

    override fun showMessage(resId: Int) {
        showMessage(getString(resId))
    }

    override fun showMessage(message: String) {
        Snackbar.make(find(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
    }

    override fun setCallbackStatusText(status: String) {
        btnToggle?.text = status
    }

    override fun setTriggerPhrase(trigger: Editable) {
        editTriggerPhrase?.text = trigger
    }

    override fun setSpeakerEnabled(enabled: Boolean) {
        checkUseSpeaker?.isChecked = enabled
    }

    override fun getLatestTriggerPhrase() = editTriggerPhrase?.text?.toString()

    fun EditText.addTextChangedListener(): Observable<TriggerTextWatcher> {
        return Observable.create({
            if (!it.isUnsubscribed) {
                val watcher: TextWatcher = object : TextWatcher {
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        it.onNext(TriggerTextWatcher(TextWatcherEvent.onChange, s))
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        it.onNext(TriggerTextWatcher(TextWatcherEvent.willChange, s))
                    }

                    override fun afterTextChanged(s: Editable?) {
                        it.onNext(TriggerTextWatcher(TextWatcherEvent.afterChange, s))
                    }
                }
                addTextChangedListener(watcher)
                val removeListener = { removeTextChangedListener(watcher) }
                it.add(object : MainThreadSubscription() {
                    override fun onUnsubscribe() {
                        removeListener()
                    }
                })
            }
        })
    }

    enum class TextWatcherEvent {
        onChange,
        willChange,
        afterChange
    }

    data class TriggerTextWatcher(val eventType: TextWatcherEvent, val value: CharSequence?)
}
