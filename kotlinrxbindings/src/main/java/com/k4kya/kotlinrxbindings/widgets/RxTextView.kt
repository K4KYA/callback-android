package com.k4kya.kotlinrxbindings.widgets

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import rx.Observable
import rx.android.MainThreadSubscription

open class TextEvent(open val value: CharSequence?) {
    override fun equals(other: Any?): Boolean {
        if (other != null && other is TextEvent) {
            return other.value?.toString()?.equals(this.value.toString()) ?: false
        }
        return false
    }

    override fun toString(): String = "Value: " + this.value
    override fun hashCode(): Int = value?.hashCode() ?: 0
}

class TextChangedEvent(override val value: CharSequence?, val start: Int, val before: Int, val count: Int) : TextEvent(value) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        if (!super.equals(other)) return false

        other as TextChangedEvent

        if (value.toString() != other.value.toString()) return false
        if (start != other.start) return false
        if (before != other.before) return false
        if (count != other.count) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (value?.hashCode() ?: 0)
        result = 31 * result + start
        result = 31 * result + before
        result = 31 * result + count
        return result
    }

    override fun toString() = "TextChangedEvent(value=$value, start=$start, before=$before, count=$count)"

}

class BeforeTextChangedEvent(override val value: CharSequence?, val start: Int, val count: Int, val after: Int) : TextEvent(value) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        if (!super.equals(other)) return false

        other as BeforeTextChangedEvent

        if (value.toString() != other.value.toString()) return false
        if (start != other.start) return false
        if (count != other.count) return false
        if (after != other.after) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (value?.hashCode() ?: 0)
        result = 31 * result + start
        result = 31 * result + count
        result = 31 * result + after
        return result
    }

    override fun toString() = "BeforeTextChangedEvent(value=$value, start=$start, count=$count, after=$after)"

}
class AfterTextChangedEvent(override val value: CharSequence?) : TextEvent(value)

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

