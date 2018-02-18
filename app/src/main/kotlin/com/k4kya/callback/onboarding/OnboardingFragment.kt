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
    
    private val content = listOf(
            OnboardingPage(R.drawable.ic_vpn_key_white_36dp, "Create a trigger phrase", ""),
            OnboardingPage(R.drawable.ic_message_white_36dp, "Receive the trigger over SMS", ""),
            OnboardingPage(R.drawable.ic_phone_in_talk_white_36dp, "Your phone will callback the sender!", "")
    )
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val index = arguments!!.getInt(ARG_ONBOARDING_INDEX)
        val rootView = inflater.inflate(R.layout.fragment_pager, container, false)
        val textView = rootView.findViewById<TextView>(R.id.section_label)
        val description = rootView.findViewById<TextView>(R.id.section_description)
        val imageView = rootView.findViewById<ImageView>(R.id.section_img)
        val screenContent = content[index]
        textView.text = screenContent.title
        description.text = screenContent.description
        imageView.setBackgroundResource(screenContent.imageRes)
        return rootView
    }
    
    companion object {
        const val ARG_ONBOARDING_INDEX = "onboarding_index"
        
        fun createInstance(section: Int): OnboardingFragment {
            return OnboardingFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ONBOARDING_INDEX, section)
                }
            }
        }
    }
}

data class OnboardingPage(val imageRes: Int, val title: String, val description: String)
