package usb;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.shifen.game.jfcz.utils.CRC16Util;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;




public final class DeviceHelp implements SeralToDeviceHelp {


    public OnDataReceiveListener onDataReceiveListener;


    public static byte DELIVER_GOODS = (byte)52;
    public static byte DELIVER_GOODS_RETURN = (byte)53;
    public static byte CHECK_STATE = (byte)54;
    public static byte CHECK_STATE_RETURN = (byte)55;
    public static byte CHECK_GOOD_SNUM = (byte)56;
    public static byte CHECK_GOOD_SNUM_RETURN = (byte)57;
    public static byte RESET = (byte)58;
    public static byte RESET_RETURN = (byte)59;
    public static byte CHECK_TEMPERATURE = (byte)60;
    public static byte CHECK_TEMPERATURE_RETURN = (byte)61;
    public static byte BUFF_START = (byte)129;
    public static byte BUFF_OVER = (byte)250;

    public static final String path = "/dev/ttyS0";
    public static final int baudrate = 9600;

    public static DeviceHelp deviceHelp;


    public SerialHandler serialHandler;

    public static DeviceHelp getInstance() {
        if (deviceHelp == null) {
            synchronized (DeviceHelp.class) {
                if (deviceHelp == null) {
                    deviceHelp = new DeviceHelp();
                }
            }
        }
        return deviceHelp;
    }

    public void initDevice(@Nullable SerialHandler s) {
        SerialPortUtil.getInstance().initSerialPort(path, baudrate, 0);
        SerialPortUtil.getInstance().setOnDataReceiveListener(this);
        serialHandler = s;


    }

    public void setOnDataReceiveListener(OnDataReceiveListener l) {
        this.onDataReceiveListener = l;
    }

    public final void sendData( byte[] bytes) {
        Log.i("ooo", "=======sendData==========");
        Log.i("ooo", this.bytesToHexString(bytes));
        SerialPortUtil.getInstance().sendBuffer(bytes);
    }


    public void onSeralToDeviceHelp(byte[] bytes) {

        Bundle bundle = new Bundle();
        bundle.putByteArray("key", bytes);
        Message message = serialHandler.obtainMessage();
        message.what = 100;
        message.setData(bundle);
        serialHandler.sendMessage(message);
    }

    public final void updateData(byte [] bytes) {

        Log.i("ooo", "=======onDataReceiveBuffListener==========");
        Log.i("ooo", this.bytesToHexString(bytes));
        if(this.onDataReceiveListener != null) {
            onDataReceiveListener.onDataReceive(bytes);
        }


       /* val operateStatusBody = operateStatusBody(number,status)
        val gson = Gson()
        val json = gson.toJson(operateStatusBody)
        val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json)
        ServiceManager.create(OperateService::class.java).operateStatus(body).observeOnMain {}*/
    }


    public final void deliverGoods(int num) {
        Log.i("JFCZApplication", "deliverGoods: " + num);
        byte byte0 = DELIVER_GOODS;
        byte[] byteArrayOf = new byte[]{byte0, (byte)num};
        byte[] bytes = this.crcCheck(byteArrayOf);
        this.sendData(bytes);
    }


    public final void checkState(Integer num) {
        byte byte0 = CHECK_STATE;
        byte byte1 = num == null?0:(byte)num.intValue();
        byte[] byteArrayOf = new byte[]{byte0, byte1};
        byte[] bytes = this.crcCheck(byteArrayOf);
        this.sendData(bytes);
    }

    public final void checkGoodsNum(Integer num) {
        byte byte0 = CHECK_GOOD_SNUM;
        byte byte1 = num == null?0:(byte)num.intValue();
        byte[] byteArrayOf = new byte[]{byte0, byte1};
        byte[] bytes = this.crcCheck(byteArrayOf);
        this.sendData(bytes);
    }

    public final void reset(Integer num) {
        byte byte0 = RESET;
        byte byte1 = num == null?0:(byte)num.intValue();
        byte[] byteArrayOf = new byte[]{byte0, byte1};
        byte[] bytes = this.crcCheck(byteArrayOf);
        this.sendData(bytes);
    }

    public final void checkTemperature() {
        byte byte0 = CHECK_TEMPERATURE;
        int byte1 = 0;
        byte[] byteArrayOf = new byte[]{byte0, (byte)byte1};
        byte[] bytes = this.crcCheck(byteArrayOf);
        this.sendData(bytes);
    }


    public final byte[] crcCheck(byte[] byteArray) {
        byte[] newArr = CRC16Util.appendCrc16(byteArray);
        byte[] bytes = new byte[]{(byte)129, 1, (byte)byteArray.length, newArr[0], newArr[1], newArr[3], newArr[2], (byte)250};
        return bytes;
    }


    public final String bytesToHexString(@Nullable byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if(src != null && src.length > 0) {
            int i = 0;
            int var4 = src.length - 1;
            if(i <= var4) {
                while(true) {
                    int v = src[i] & 255;
                    String hv = Integer.toHexString(v);
                    stringBuilder.append(" 0x");
                    if(hv.length() < 2) {
                        stringBuilder.append(0);
                        stringBuilder.append(hv);
                    } else {
                        stringBuilder.append(hv);
                    }

                    if(i == var4) {
                        break;
                    }

                    ++i;
                }
            }

            return stringBuilder.toString();
        } else {
            return null;
        }
    }


}
