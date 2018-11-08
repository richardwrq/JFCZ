package com.shifen.game.jfcz

import android.app.Application
import android.util.Log
import com.google.gson.Gson
import com.shifen.game.jfcz.model.*

import com.shifen.game.jfcz.services.PushService
import com.shifen.game.jfcz.services.ServiceManager
import com.shifen.game.jfcz.services.observeOnMain

import com.shifen.game.jfcz.utils.*
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.IUmengRegisterCallback
import com.umeng.message.MsgConstant
import com.umeng.message.PushAgent
import io.reactivex.disposables.CompositeDisposable
import okhttp3.RequestBody
import usb.DeviceHelp
import usb.SerialHandler
import java.util.*
import android.app.ActivityManager
import android.content.Context
import com.shifen.game.jfcz.restart.CrashHandler
import com.shifen.game.jfcz.services.OperateService
import usb.OnDataReceiveListener


class JFCZApplication() : Application() {

    companion object {
        lateinit var INSTANCE: JFCZApplication;
        var response = Vector<Byte>()

    }
    val DEVICE_ID by lazy { getIMEI(this) }
    val compositeDisposable = CompositeDisposable()
    public override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        //val crashHandler = CrashHandler.getInstance()
        //crashHandler.init(applicationContext)

        initUM()


        val processName =getProcessName(this, android.os.Process.myPid())
        if (processName != null) {
            val defaultProcess = processName == "com.shifen.game.jfcz"
            if (defaultProcess) {
                //当前应用的初始化
                var  serialHandler = SerialHandler.getInstance()
                DeviceHelp.getInstance().initDevice(serialHandler)
                initStatus()

                ConfigManager.init(this)
                ApiConfig.token = getConfig().getString(APP_TOKEN, "")!!
                ApiConfig.containerId = getConfig().getString(CONTAINER_ID, "")!!
            }
        }

    }



    fun getProcessName(cxt: Context, pid: Int): String? {
        val am = cxt.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApps = am.runningAppProcesses ?: return null
        for (procInfo in runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName
            }
        }
        return null
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
                ServiceManager.create(PushService::class.java).bind(body)
                        .observeOnMain{}

            }
            override fun onFailure(p0: String, p1: String) {
                Log.d("JFCZApplication", "um register failed!")
            }
        })
    }
    fun initStatus(){
        val thread = Thread(Runnable {
            while (true) {
                DeviceHelp.getInstance().checkState(null)
                DeviceHelp.getInstance().setOnDataReceiveListener(object : OnDataReceiveListener {
                    override fun onDataReceive(msg: ByteArray) {
                        var itemArray = ArrayList<operateStatusBody>();
                        for (i in 0..(msg[2].toInt()-1)) {
                            var status = msg[3 + i].toInt()
                            // 00 关闭 01 开启 02 未知
                            // houtai 0：关闭，1：开启，2：禁止，3：异常
                            if (msg[3 + i].toInt() == 0x00) {
                                status = 0
                            }
                            if (msg[3 + i].toInt() == 0x01) {
                                status = 1
                            }
                            // TODO
                            status = 0
                            var item = operateStatusBody(i, status);
                            itemArray.add(item)
                        }
                        val gson = Gson()
                        val json = gson.toJson(itemArray)
                        val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json)
                        ServiceManager.create(OperateService::class.java).operateStatusAll(body)
                                .observeOnMain{}

                    }
                })

                try {
                    Thread.sleep(30 * 60 * 1000L)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
        })
        thread.start();
    }
}