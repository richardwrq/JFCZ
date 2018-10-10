package com.shifen.game.jfcz.utils

import android.graphics.Bitmap
import com.shifen.game.jfcz.BuildConfig
import com.uuzuche.lib_zxing.activity.CodeUtils

object ApiConfig {
    const val CONNECT_TIMEOUT = 5L
    const val READ_TIMEOUT = 10L
    const val WRITE_TIMEOUT = 10L
    const val BASE_URL = BuildConfig.BASE_URL

    var token = ""

    var containerId = ""

    fun generateQRCode(goodsId: Long, type: Int, w: Int, h: Int): Bitmap {
        val timestamp = System.currentTimeMillis() / 1000
        val deviceId = "1000000000000001"//getIMEI(JFCZApplication.INSTANCE)
        val sign = (timestamp.toString() + goodsId + ApiConfig.containerId + deviceId + type + BuildConfig.API_KEY).md5()
        val url = "${ApiConfig.BASE_URL}/pay/scan2pay?t=$timestamp&goodsid=$goodsId&gid=${ApiConfig.containerId}&devid=$deviceId&type=$type&sign=$sign"
        return CodeUtils.createImage(url, w, h, null)
    }
}