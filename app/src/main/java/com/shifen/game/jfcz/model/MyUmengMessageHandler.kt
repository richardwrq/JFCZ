package com.shifen.game.jfcz.model

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.shifen.game.jfcz.services.ServiceManager
import com.shifen.game.jfcz.ui.ADActivity
import com.shifen.game.jfcz.utils.*
import com.umeng.message.UmengMessageHandler
import com.umeng.message.entity.UMessage

class MyUmengMessageHandler : UmengMessageHandler() {

    /**
     * 通知的回调方法（通知送达时会回调）
     * @param p0 Context?
     * @param p1 UMessage?
     */
    override fun dealWithNotificationMessage(p0: Context, p1: UMessage) {
        super.dealWithNotificationMessage(p0, p1)
        Log.d("MyUmengMessageHandler", "receive message: ${p1.custom}")
    }

    /**
     * 自定义消息的回调方法
     * @param p0 Context?
     * @param p1 UMessage?
     */
    override fun dealWithCustomMessage(p0: Context, p1: UMessage) {
        super.dealWithCustomMessage(p0, p1)
        Log.d("MyUmengMessageHandler", "receive custom message: ${p1.custom}")
        if (p1.extra["type"] == "1") {
//            ServiceManager.create()
        } else if (p1.extra["type"] == "2") {

        }
        p0.putConfig {
            val containerId = p1.extra["containerId"]
            val gameVersion = p1.extra["gameVersion"]
            val gridVersion = p1.extra["gridVersion"]
            val goodsVersion = p1.extra["goodsVersion"]
            if (!containerId.isNullOrEmpty()) {
                ApiConfig.containerId = containerId!!
                it.putString(CONTAINER_ID, containerId)
            }
            if (!gameVersion.isNullOrEmpty()) {
                it.putString(GAME_VERSION, gameVersion)
            }
            if (!gridVersion.isNullOrEmpty()) {
                it.putString(GRID_VERSION, gridVersion)
            }
            if (!goodsVersion.isNullOrEmpty()) {
                it.putString(GOODS_VERSION, goodsVersion)
            }

//            if (!images.isNullOrBlank()) {
//                it.putString(BANNER_LIST, images)
//                LocalBroadcastManager.getInstance(p0).sendBroadcast(Intent(ADActivity.ACTION_REFRESH_BANNER))
//            }
        }
    }
}