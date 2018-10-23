package com.shifen.game.jfcz.usb;


import android.serialport.SerialPortFinder;

/**
 * Created by Administrator on 2018/10/23.
 */

public class DeviceHelp {

    /**
     * 初始化设备列表
     */
    public static void initDevice() {

        SerialPortFinder serialPortFinder = new SerialPortFinder();

        String[] mDevices = serialPortFinder.getAllDevicesPath();
        if (mDevices.length == 0) {
            mDevices = new String[] {
                 // todo err
            };
        }
        // 波特率
        Device mDevice = new Device("/dev/ttyS0","9600");
        SerialPortManager.instance().close();
        SerialPortManager.instance().open(mDevice);
    }


    public static void sendData(byte[] bytes) {
        SerialPortManager.instance().sendCommand(bytes);

    }


}
