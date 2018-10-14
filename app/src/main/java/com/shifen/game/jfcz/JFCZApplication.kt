package com.shifen.game.jfcz

import android.app.Application
import android.util.Log
import com.google.gson.Gson
import com.shifen.game.jfcz.model.MyUmengMessageHandler
import com.shifen.game.jfcz.model.PushBindRequestBody
import com.shifen.game.jfcz.services.PushService
import com.shifen.game.jfcz.services.ServiceManager
import com.shifen.game.jfcz.utils.*
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.IUmengRegisterCallback
import com.umeng.message.MsgConstant
import com.umeng.message.PushAgent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody


class JFCZApplication : Application() {

    companion object {
        lateinit var INSTANCE: JFCZApplication
    }

    val DEVICE_ID by lazy { getIMEI(this) }
    val compositeDisposable = CompositeDisposable()

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        initUM()

        ConfigManager.init(this)
        ApiConfig.token = getConfig().getString(APP_TOKEN, "")!!
        ApiConfig.containerId = getConfig().getString(CONTAINER_ID, "")!!
    }

    private fun initUM() {

        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "72fe2d62f461225525dd7e46a9babc6b")

        val mPushAgent = PushAgent.getInstance(this)
        // 响铃、振动、呼吸灯服务端控制
        // MsgConstant.NOTIFICATIONPLAYSERVER（服务端控制）
        // MsgConstant.NOTIFICATIONPLAYSDKENABLE（客户端允许）
        // MsgConstant.NOTIFICATIONPLAYSDKDISABLE（客户端禁止）
        mPushAgent.notificationPlaySound = MsgConstant.NOTIFICATION_PLAY_SERVER
        mPushAgent.notificationPlayVibrate = MsgConstant.NOTIFICATION_PLAY_SERVER
        mPushAgent.notificationPlayLights = MsgConstant.NOTIFICATION_PLAY_SERVER

        mPushAgent.messageHandler = MyUmengMessageHandler()

        mPushAgent.register(object : IUmengRegisterCallback {
            override fun onSuccess(p0: String) {
                Log.d("JFCZApplication", "um register success! $p0")
                val pushBindRequestBody = PushBindRequestBody(DEVICE_ID, p0)
                val gson = Gson()
                val json = gson.toJson(pushBindRequestBody)
                val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json)
                compositeDisposable.add(ServiceManager.create(PushService::class.java).bind(body)
                        .subscribeOn(Schedulers.io())
                        .subscribe {
                            Log.i("JFCZApplication", "bind result: $it")
                        })
            }

            override fun onFailure(p0: String, p1: String) {
                Log.d("JFCZApplication", "um register failed!")
            }
        })
    }

}