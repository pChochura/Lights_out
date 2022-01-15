package com.pointlessapps.lightsout

import android.content.res.Resources

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.sp: Int
    get() = (this / Resources.getSystem().displayMetrics.scaledDensity).toInt()

val Float.px: Float
    get() = (this * Resources.getSystem().displayMetrics.density).toInt().toFloat()

val Float.dp: Float
    get() = (this / Resources.getSystem().displayMetrics.density).toInt().toFloat()

val Float.sp: Float
    get() = (this * Resources.getSystem().displayMetrics.scaledDensity).toInt().toFloat()
