package com.shifen.game.jfcz.usb

import android.serialport.SerialPortFinder
import android.util.Log
import com.google.gson.Gson
import com.shifen.game.jfcz.model.operateStatusBody
import com.shifen.game.jfcz.services.OperateService
import com.shifen.game.jfcz.services.ServiceManager
import com.shifen.game.jfcz.services.observeOnMain
import com.shifen.game.jfcz.utils.CRC16Util
import okhttp3.RequestBody

/**
 * Created by Administrator on 2018/10/23.
 */
object DeviceHelp{

    fun initDevice() {

        val path = "/dev/ttyS0"
        val baudrate = "9600"
        val serialPortFinder = SerialPortFinder()

        var mDevices = serialPortFinder.allDevicesPath
        if (mDevices.size == 0) {
            mDevices = arrayOf()
        }
        // 波特率
        val mDevice = Device(path, baudrate)
        SerialPortManager.instance().close()
        SerialPortManager.instance().open(mDevice)
    }


    fun sendData(bytes: ByteArray) {
        Log.i("JFCZApplication", "=======sendData==========")
        Log.i("JFCZApplication", bytesToHexString(bytes))
        SerialPortManager.instance().sendCommand(bytes)
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
        val newArr = CRC16Util.appendCrc16(byteArray);
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