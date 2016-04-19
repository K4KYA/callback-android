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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        setupViewPager()
        setupButtons()
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
        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                currentPage = position
                updateIndicators(currentPage)
                viewPager?.setBackgroundColor(colours[position])
                nextButton?.visibility = if (position == 2) View.GONE else View.VISIBLE
                finishButton?.visibility = if (position == 2) View.VISIBLE else View.GONE
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                var updatedColour = evaluator.evaluate(
                        positionOffset,
                        colours[position],
                        colours[( if (position == 2) position else position + 1)]
                ) as Int
                viewPager?.setBackgroundColor(updatedColour)
            }
        })
    }
}