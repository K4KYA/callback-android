package com.k4kya.kotlinrxbindings.widgets

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import rx.Observable
import rx.android.MainThreadSubscription

sealed class TextEvent(open val value: CharSequence?)
data class TextChangedEvent(override val value: CharSequence?, val start: Int, val before: Int, val count: Int) : TextEvent(value)
data class BeforeTextChangedEvent(override val value: CharSequence?, val start: Int, val count: Int, val after: Int) : TextEvent(value)
data class AfterTextChangedEvent(override val value: CharSequence?) : TextEvent(value)

fun TextView.textEvents(): Observable<TextEvent> {
    return Observable.create {
        if (!it.isUnsubscribed) {
            val watcher = object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    it.onNext(TextChangedEvent(s, start, before, count))
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    it.onNext(BeforeTextChangedEvent(s, start, count, after))
                }

                override fun afterTextChanged(s: Editable?) {
                    it.onNext(AfterTextChangedEvent(s))
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
    }
}

