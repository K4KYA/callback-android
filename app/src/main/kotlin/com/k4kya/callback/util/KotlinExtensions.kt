package com.k4kya.callback.util

import android.app.Activity
import android.content.Intent
import android.support.v4.app.Fragment

/**
 * Created by amal on 17/12/2017.
 */

inline fun <reified T: Activity> Activity.intentFor() = Intent(this, T::class.java)
inline fun <reified T: Activity> Fragment.intentFor() = Intent(context, T::class.java)
inline fun <reified T: Activity> android.app.Fragment.intentFor() = Intent(activity, T::class.java)