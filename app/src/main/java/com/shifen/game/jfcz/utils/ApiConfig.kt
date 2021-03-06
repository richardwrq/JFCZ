package com.shifen.game.jfcz.utils

import android.graphics.Bitmap
import com.shifen.game.jfcz.BuildConfig
import com.shifen.game.jfcz.JFCZApplication
import com.shifen.game.jfcz.model.Config
import com.uuzuche.lib_zxing.activity.CodeUtils

object ApiConfig {
    const val CONNECT_TIMEOUT = 5L
    const val READ_TIMEOUT = 10L
    const val WRITE_TIMEOUT = 10L
    const val BASE_URL = BuildConfig.BASE_URL

    var token = ""

    var containerId = ""

    var timestamp = 0L;

    var config: Config? = null

    fun generateQRCode(goodsId: String, gridId :String ,type: Int, w: Int, h: Int): Bitmap {
        val timestamp = System.currentTimeMillis() / 1000
        ApiConfig.timestamp = timestamp;
        val sign = ("t="+timestamp.toString()+"&goodsid=" + goodsId +"&gid=" + gridId +"&devid="+ ApiConfig.containerId  + "&type=" +type + "&key="+BuildConfig.API_KEY).md5()
        val url = "${ApiConfig.BASE_URL}/pay/scan2pay?t=$timestamp&goodsid=$goodsId&gid=$gridId&devid=${ApiConfig.containerId}&type=$type&sign=$sign"
        return CodeUtils.createImage(url, w, h, null)
    }
}