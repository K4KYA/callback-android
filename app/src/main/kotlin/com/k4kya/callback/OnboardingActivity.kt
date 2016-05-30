package com.k4kya.callback

import android.animation.ArgbEvaluator
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import com.k4kya.kotlinrxbindings.views.PageChangeEventType
import com.k4kya.kotlinrxbindings.views.pageChanges
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers

class OnboardingActivity : AppCompatActivity() {

    val colours: List<Int>
        get() {
            return listOf(
                    ContextCompat.getColor(this, R.color.colorPrimary),
                    ContextCompat.getColor(this, R.color.colorAccent),
                    ContextCompat.getColor(this, R.color.action_red))
        }

    val evaluator = ArgbEvaluator()

    var currentPage = 0

    val indicators: List<ImageView>
        get() {
            return listOf(
                    findViewById(R.id.intro_indicator_0) as ImageView,
                    findViewById(R.id.intro_indicator_1) as ImageView,
                    findViewById(R.id.intro_indicator_2) as ImageView
            )
        }

    val viewPager: ViewPager?
        get() {
            return findViewById(R.id.viewPager) as ViewPager?
        }

    val nextButton: ImageButton?
        get() {
            return findViewById(R.id.intro_btn_next) as ImageButton?
        }

    val finishButton: Button?
        get() {
            return findViewById(R.id.intro_btn_finish) as Button?
        }

    val skipButton: Button?
        get() {
            return findViewById(R.id.intro_btn_skip) as Button?
        }

    val sectionsPagerAdapter: OnboardingPagerAdapter
        get() {
            return OnboardingPagerAdapter(supportFragmentManager)
        }

    private var viewPagerEvents: Subscription? = null

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
        viewPagerEvents?.unsubscribe()
    }

    fun setupButtons() {
        nextButton?.setOnClickListener {
            currentPage += 1
            viewPager?.setCurrentItem(currentPage, true)
        }
        skipButton?.setOnClickListener { finish() }
        finishButton?.setOnClickListener { finish() }
    }

    fun updateIndicators(currentPage: Int) {
        indicators.mapIndexed {
            i, imageView ->
            imageView.setBackgroundResource(if (i == currentPage) R.drawable.indicator_selected else R.drawable.indicator_unselected)
        }
    }

    fun setupViewPager() {
        viewPager?.adapter = sectionsPagerAdapter
        viewPagerEvents = viewPager?.pageChanges()
                ?.filter { it.type == PageChangeEventType.pageScrolled || it.type == PageChangeEventType.pageSelected }
                ?.subscribeOn(AndroidSchedulers.mainThread())
                ?.subscribe {
                    @Suppress("NON_EXHAUSTIVE_WHEN")
                    when (it.type) {
                        PageChangeEventType.pageScrolled -> {
                            if (it.position != null && it.positionOffset != null) {
                                val position = it.position!!
                                val positionOffset = it.positionOffset!!
                                val updatedColour = evaluator.evaluate(
                                        positionOffset,
                                        colours[position],
                                        colours[(if (position == 2) position else position + 1)]
                                ) as Int
                                viewPager?.setBackgroundColor(updatedColour)
                            }
                        }
                        PageChangeEventType.pageSelected -> {
                            currentPage = it.position!!
                            updateIndicators(currentPage)
                            viewPager?.setBackgroundColor(colours[currentPage])
                            nextButton?.visibility = if (currentPage == 2) View.GONE else View.VISIBLE
                            finishButton?.visibility = if (currentPage == 2) View.VISIBLE else View.GONE
                        }
                    }
                }
    }
}