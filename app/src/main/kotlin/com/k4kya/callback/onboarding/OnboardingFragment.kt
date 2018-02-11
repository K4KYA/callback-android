package com.k4kya.callback.onboarding

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.k4kya.callback.R

class OnboardingFragment : Fragment() {

    private val content: List<OnboardingPage>
        get() {
            return listOf(
                    OnboardingPage(R.drawable.ic_vpn_key_white_36dp, "Create a trigger phrase", ""),
                    OnboardingPage(R.drawable.ic_message_white_36dp, "Receive the trigger over SMS", ""),
                    OnboardingPage(R.drawable.ic_phone_in_talk_white_36dp, "Your phone will callback the sender!", "")
            )
        }

    companion object {
        const val ARG_SECTION_NUMBER = "section_number"

        fun createInstance(section: Int): OnboardingFragment {
            val fragment = OnboardingFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, section)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val section = arguments!!.getInt(ARG_SECTION_NUMBER)
        val rootView = inflater.inflate(R.layout.fragment_pager, container, false)
        val textView = rootView?.findViewById<TextView>(R.id.section_label)
        val screenContent = content[section - 1]
        textView?.text = screenContent.title
        val description = rootView?.findViewById<TextView>(R.id.section_description)
        description?.text = screenContent.description
        val imageView = rootView?.findViewById<ImageView>(R.id.section_img)
        imageView?.setBackgroundResource(screenContent.imageRes)
        return rootView
    }

    data class OnboardingPage(val imageRes: Int, val title: String, val description: String)

}