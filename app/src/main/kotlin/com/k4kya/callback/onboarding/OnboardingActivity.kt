package com.k4kya.callback.onboarding

import android.animation.ArgbEvaluator
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import com.k4kya.callback.R
import com.k4kya.callback.util.bindView
import com.k4kya.callback.util.bindViews
import com.k4kya.kotlinrxbindings.views.PAGE_SCROLLED
import com.k4kya.kotlinrxbindings.views.PAGE_SELECTED
import com.k4kya.kotlinrxbindings.views.PageChangeEvent
import com.k4kya.kotlinrxbindings.views.pageChanges
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers

class OnboardingActivity : AppCompatActivity() {
    
    private val colours: List<Int>
        get() = listOf(
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorAccent),
                ContextCompat.getColor(this, R.color.action_red)
        )
    private val evaluator = ArgbEvaluator()
    private var currentPage = 0
    private val indicators: List<ImageView> by bindViews(
            R.id.intro_indicator_0,
            R.id.intro_indicator_1,
            R.id.intro_indicator_2
    )
    private val viewPager: ViewPager by bindView(R.id.viewPager)
    private val nextButton: Button by bindView(R.id.intro_btn_next)
    private val skipButton: Button by bindView(R.id.intro_btn_skip)
    private lateinit var viewPagerEvents: Subscription
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        setupButtons()
    }
    
    override fun onResume() {
        super.onResume()
        setupViewPager()
    }
    
    override fun onPause() {
        super.onPause()
        if (::viewPagerEvents.isInitialized) {
            viewPagerEvents.unsubscribe()
        }
    }
    
    override fun onBackPressed() {
        if (currentPage == 0) {
            super.onBackPressed()
        } else {
            currentPage -= 1
            viewPager.setCurrentItem(currentPage, true)
        }
    }
    
    private fun setupButtons() {
        nextButton.setOnClickListener {
            if (currentPage == 2) {
                finish()
            } else {
                currentPage += 1
                viewPager.setCurrentItem(currentPage, true)
            }
        }
        skipButton.setOnClickListener { finish() }
    }
    
    private fun updateIndicators(currentPage: Int) {
        indicators.forEachIndexed { i, imageView ->
            imageView.setBackgroundResource(if (i == currentPage) R.drawable.indicator_selected else R.drawable.indicator_unselected)
        }
    }
    
    private fun setupViewPager() {
        viewPager.adapter = OnboardingPagerAdapter(supportFragmentManager)
        viewPagerEvents = viewPager.pageChanges()
                .filter { it.type == PAGE_SCROLLED || it.type == PAGE_SELECTED }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it.type) {
                        PAGE_SCROLLED -> {
                            pageScrolled(it)
                        }
                        PAGE_SELECTED -> {
                            pageSelected(it)
                        }
                    }
                }, {
                    it.printStackTrace()
                })
    }
    
    private fun pageSelected(event: PageChangeEvent) {
        currentPage = event.position!!
        updateIndicators(currentPage)
        viewPager.setBackgroundColor(colours[currentPage])
        nextButton.text = getString(if (currentPage == 2) R.string.Done else R.string.next_button)
    }
    
    private fun pageScrolled(event: PageChangeEvent) {
        if (event.position != null && event.positionOffset != null) {
            val position = event.position!!
            val positionOffset = event.positionOffset!!
            val updatedColour = evaluator.evaluate(
                    positionOffset,
                    colours[position],
                    colours[(if (position == 2) position else position + 1)]
            ) as Int
            viewPager.setBackgroundColor(updatedColour)
        }
    }
}