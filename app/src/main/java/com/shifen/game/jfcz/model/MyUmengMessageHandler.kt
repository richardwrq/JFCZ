package com.shifen.game.jfcz.model

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.shifen.game.jfcz.ConfigManager
import com.shifen.game.jfcz.services.*
import com.shifen.game.jfcz.ui.ADActivity
import com.shifen.game.jfcz.utils.*
import com.umeng.message.UmengMessageHandler
import com.umeng.message.entity.UMessage
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

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

        if (type ==1){
            ServiceManager.create(BannerService::class.java)
                    .getBannerList()
                    .observeOnMain{
                        ConfigManager.updateBannerList(it.data)
                    }

        }

        if (type ==3){

            ServiceManager.create(GameService::class.java)
                    .getGameConfig()
                    .observeOnMain{
                        ConfigManager.updateGameConfig(it.data)
                    }
        }

    /*    if (p1.extra["type"] == "1") {
            ServiceManager.create(ConfigService::class.java)
                    .getConfig()
                    .wrapLogin()
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : DisposableObserver<Response<Config>>() {

                        override fun onComplete() {

                        }

                        override fun onNext(t: Response<Config>) {
                            if (t.code != 0) {
                                Log.e(tag, "get config error: ${t.message}")
                                return
                            }
                            if (p0.getConfig().getString(GAME_VERSION, "") != t.data.gameVersion) {
                                ServiceManager.create(GameService::class.java)
                                        .getGameConfig()
                                        .observeOnMain {
                                            ConfigManager.updateGameConfig(it.data)
                                        }
                            }
                            if (p0.getConfig().getString(GOODS_VERSION, "") != t.data.goodsVersion) {
                                ServiceManager.create(GiftService::class.java)
                                        .getGiftList()
                                        .observeOnMain {
                                            ConfigManager.updateGiftList(it.data)
                                        }
                            }
                            p0.putConfig {
                                ApiConfig.containerId = t.data.containerId
                                ApiConfig.config = t.data
                                it.putString(CONTAINER_ID, t.data.containerId)
                                it.putString(GAME_VERSION, t.data.gameVersion)
                                it.putString(GRID_VERSION, t.data.gridVersion)
                                it.putString(GOODS_VERSION, t.data.goodsVersion)
                            }
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                        }
                    })
        } else if (p1.extra["type"] == "2") {
            LocalBroadcastManager.getInstance(p0).sendBroadcast(Intent(ADActivity.ACTION_REFRESH_BANNER))
        }


//            if (!images.isNullOrBlank()) {
//                it.putString(BANNER_LIST, images)
//                LocalBroadcastManager.getInstance(p0).sendBroadcast(Intent(ADActivity.ACTION_REFRESH_BANNER))
//            }*/
    }
}