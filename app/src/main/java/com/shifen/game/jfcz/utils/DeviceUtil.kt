package com.shifen.game.jfcz.utils

import android.content.Context
import android.net.ConnectivityManager
import android.provider.Settings


fun getIMEI(context: Context): String {
//    val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val androidID = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
//    val id = androidID + Build.SERIAL
    return androidID
}

/**
 *
 * @param context Context
 * @return Boolean
 */
fun isNetworkAvailable(context: Context): Boolean {
    val manager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
    val networkinfo = manager.activeNetworkInfo
    return networkinfo != null && networkinfo.isAvailable
}