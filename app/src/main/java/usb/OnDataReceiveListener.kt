package usb

/**
 * Created by Administrator on 2018/10/28.
 */
interface OnDataReceiveListener {
    fun onDataReceive(msg: ByteArray)  //在接收函数中解析收到的数据
}