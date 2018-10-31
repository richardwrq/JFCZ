package com.shifen.game.jfcz.model

import android.content.Context
import android.content.Intent
import android.util.Log
import com.shifen.game.jfcz.ConfigManager
import com.shifen.game.jfcz.JFCZApplication
import com.shifen.game.jfcz.newapk.Download
import com.shifen.game.jfcz.newapk.DownloadListener
import com.shifen.game.jfcz.services.*
import com.shifen.game.jfcz.ui.OutTestActivity
import com.umeng.message.UmengMessageHandler
import com.umeng.message.entity.UMessage
import org.json.JSONObject
import usb.DeviceHelp

class MyUmengMessageHandler : UmengMessageHandler() {

    private val tag = MyUmengMessageHandler::class.java.simpleName
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

        val jsonObj: JSONObject = JSONObject(p1.custom)
        var type=jsonObj.getInt("type")

        // 更新Banner
        if (type ==1){
            ServiceManager.create(BannerService::class.java)
                    .getBannerList()
                    .observeOnMain{
                        ConfigManager.updateBannerList(it.data)
                    }

        }

        // 更新游戏配置
        if (type ==3){
            ServiceManager.create(GameService::class.java)
                    .getGameConfig()
                    .observeOnMain{
                        ConfigManager.updateGameConfig(it.data)
                    }
        }
        // 更新中奖率
        if (type ==4){
            var rate = jsonObj.getInt("rate")
            var randomType= jsonObj.getInt("randomType")
            var probability =GameProbability(type,rate,randomType)
            ConfigManager.updateGameProbability(probability)
        }

        // 下载并更新
        if(type==5){
            // val url = "http://testimages.emomo.cc/images/app-debug1.apk"
            //var url=jsonObj.getString("url")
            var url = "http://testimages.emomo.cc/images/app-debug1.apk"
            Download.getInstance().start(url)
        }

        // 进入输出测试界面
        if (type ==6){
            JFCZApplication.INSTANCE.startActivity(Intent(JFCZApplication.INSTANCE, OutTestActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }


        // 弹出货物
        if (type ==7){
            var number=jsonObj.getInt("number")
            DeviceHelp.getInstance().deliverGoods(number)
        }

    }
}