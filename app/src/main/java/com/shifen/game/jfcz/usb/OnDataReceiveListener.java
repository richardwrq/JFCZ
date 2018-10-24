package com.shifen.game.jfcz.usb;

/**
 * Created by Administrator on 2018/10/24.
 */

public interface OnDataReceiveListener {
    public void onDataReceive(byte[] buffer,int size); //在接收函数中解析收到的数据
}