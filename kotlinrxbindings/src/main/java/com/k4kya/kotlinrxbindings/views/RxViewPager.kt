package com.k4kya.kotlinrxbindings.views

import android.support.v4.view.ViewPager
import rx.Observable
import rx.android.MainThreadSubscription

const val PAGE_SELECTED = 0
const val PAGE_SCROLL_STATE_CHANGED = 1
const val PAGE_SCROLLED = 2

data class PageChangeEvent(val type: Int, val position: Int? = null, val state: Int? = null, val positionOffset: Float? = null, val positionOffsetPixels: Int? = null)

fun ViewPager.pageChanges(): Observable<PageChangeEvent> {
    return Observable.create {
        if (!it.isUnsubscribed) {
            val pageChangeListener = object : ViewPager.OnPageChangeListener {
                override fun onPageSelected(position: Int) {
                    it.onNext(PageChangeEvent(PAGE_SELECTED, position = position))
                }

                override fun onPageScrollStateChanged(state: Int) {
                    it.onNext(PageChangeEvent(PAGE_SCROLL_STATE_CHANGED, state = state))
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    it.onNext(PageChangeEvent(PAGE_SCROLLED, position = position, positionOffset = positionOffset, positionOffsetPixels = positionOffsetPixels))
                }
            }
            addOnPageChangeListener(pageChangeListener)
            val removeListener = { removeOnPageChangeListener(pageChangeListener) }
            it.add(object : MainThreadSubscription() {
                override fun onUnsubscribe() {
                    removeListener()
                }
            })
        }
    }

}

