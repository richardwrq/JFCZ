package com.shifen.game.jfcz

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import com.google.gson.Gson
import com.shifen.game.jfcz.model.*
import com.shifen.game.jfcz.services.OperateService
import com.shifen.game.jfcz.services.PushService
import com.shifen.game.jfcz.services.ServiceManager
import com.shifen.game.jfcz.services.observeOnMain
import com.shifen.game.jfcz.utils.*
import com.tbtech.serial.SerialOperate
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.IUmengRegisterCallback
import com.umeng.message.MsgConstant
import com.umeng.message.PushAgent
import io.reactivex.disposables.CompositeDisposable
import okhttp3.RequestBody
import java.util.*


class JFCZApplication : Application() {

    companion object {
        lateinit var INSTANCE: JFCZApplication;
        var response = Vector<Byte>()
        lateinit var serialOperate: SerialOperate
    }

    val DEVICE_ID by lazy { getIMEI(this) }
    val compositeDisposable = CompositeDisposable()
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        //val crashHandler = CrashHandler.getInstance()
        //crashHandler.init(applicationContext)

        initUM()
        initTB()
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
                ServiceManager.create(PushService::class.java).bind(body)
                        .observeOnMain{}

            }
            override fun onFailure(p0: String, p1: String) {
                Log.d("JFCZApplication", "um register failed!")
            }
        })
    }


    fun initTB() {

        var appInfo: ApplicationInfo? = null
        try {
            appInfo = this.packageManager
                    .getApplicationInfo(packageName,
                            PackageManager.GET_META_DATA)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        val TB_KEY = appInfo!!.metaData.getString("TB_KEY")
        serialOperate = SerialOperate.getInstance();
        serialOperate.initSerial(this, TB_KEY, 0, 9600, { bytes ->
            if (bytes != null) {
                response.add(bytes[0])
                if (bytes[0] == 0xFA.toByte()) {
                    val msg = ByteArray(response.size)
                    var i = 0
                    while (i < response.size) {
                        msg[i] = response[i];
                        i++
                    }
                    response.clear();
                    updateData(msg)

                }
            }
        });
    }

    fun sendData(bytes: ByteArray) {
        Log.i("JFCZApplication", "=======sendData==========")
        Log.i("JFCZApplication", bytesToHexString(bytes))
        serialOperate.sendData(bytes)
    }

    fun updateData(msg: ByteArray) {
        Log.i("JFCZApplication", "=======onDataReceiveBuffListener==========")
        Log.i("JFCZApplication", bytesToHexString(msg))

        if (msg[2] == 0x01.toByte() && msg[3] == 53.toByte()) {

            var status = 3
            if (msg[5].toInt() == 1) {
                status= 0
            }
            if (msg[5].toInt() == 0) {
                status= 1
            }
            var number = msg[4].toInt()
            // todo
            number =8
            val operateStatusBody = operateStatusBody(number,status)
            val gson = Gson()
            val json = gson.toJson(operateStatusBody)
            val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json)
            ServiceManager.create(OperateService::class.java).operateStatus(body).observeOnMain {}
        }

    }

    /**
     *@Description: 出货命令
     *@Param: num 柜门编号
     */
    fun deliverGoods(num: Int) {
        Log.i("JFCZApplication","deliverGoods: ${num}")
        // todo
        var num =8
        val byte0 = 52;
        val byte1 = num;
        val byteArrayOf = byteArrayOf(byte0.toByte(), byte1.toByte())
        val bytes =crcCheck(byteArrayOf)
        sendData(bytes)
    }

    /**
     *@Description: 查询柜门锁状态
     *@Param: num 柜门编号，不传查所有
     */
    fun checkState(num: Int?){
        val byte0 = 54;
        val byte1= if (num ==null){ 0x00 }else { num.toByte() }
        val byteArrayOf = byteArrayOf(byte0.toByte(), byte1.toByte())
        val bytes =crcCheck(byteArrayOf)
        sendData(bytes)
    }

    /**
     *@Description: 查询柜门已出货数命令
     *@Param: num 柜门编号，不传查所有
     */
    fun checkGoodsNum(num: Int?){
        val byte0 = 56;
        val byte1= if (num ==null){ 0x00 }else { num.toByte() }
        val byteArrayOf = byteArrayOf(byte0.toByte(), byte1.toByte())
        val bytes =crcCheck(byteArrayOf)
        sendData(bytes)
    }

    /**
     *@Description: 补货复位命令
     *@Param: num 柜门编号，不传查所有
     */
    fun reset(num: Int?){
        val byte0 = 58;
        val byte1= if (num ==null){ 0x00 }else { num.toByte() }
        val byteArrayOf = byteArrayOf(byte0.toByte(), byte1.toByte())
        val bytes =crcCheck(byteArrayOf)
        sendData(bytes)
    }

    /**
     *@Description: 查询温度命令
     */
    fun checkTemperature (){
        var byte0 = 60;
        var byte1 = 0x00
        val byteArrayOf = byteArrayOf(byte0.toByte(), byte1.toByte())
        var bytes =crcCheck(byteArrayOf)
        sendData(bytes)
    }

    /**
     *@Description: crc检查
     *@Param:
     */
    fun crcCheck(byteArray:ByteArray): ByteArray {
        val newArr =CRC16Util.appendCrc16(byteArray);
        val bytes =byteArrayOf(0x81.toByte(), 0x01, byteArray.size.toByte(), newArr[0], newArr[1], newArr[3], newArr[2], 0xFA.toByte())
        return bytes;
    }

    fun bytesToHexString(src: ByteArray?): String? {
        val stringBuilder = StringBuilder("")
        if (src == null || src.size <= 0) {
            return null
        }
        for (i in 0..src.size - 1) {
            val v = src[i].toInt() and 0xFF
            val hv = Integer.toHexString(v)
            stringBuilder.append(" 0x")
            if (hv.length < 2) {
                stringBuilder.append(0)
                stringBuilder.append(hv)
            }else{
                stringBuilder.append(hv)
            }
        }
        return stringBuilder.toString()
    }

}