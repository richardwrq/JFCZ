package usb;


import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.shifen.game.jfcz.JFCZApplication;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Administrator on 2018/10/24.
 */

public class SerialPortUtil {
    private final String TAG = SerialPortUtil.class.getSimpleName();

    private SerialPort serialPort = null;
    private OutputStream mOutputStream = null;
    private InputStream mInputStream = null;
    private SerialReadThread mSerialReadThread = null;        //数据接收线程
    private volatile boolean isStop = false;

    private static SerialPortUtil serialPortUtil = null;       //单例模式

    public static SerialPortUtil getInstance() {
        if (serialPortUtil == null) {
            synchronized (SerialPortUtil.class) {
                if (serialPortUtil == null) {
                    serialPortUtil = new SerialPortUtil();
                }
            }
        }
        return serialPortUtil;
    }


    /**
     * 初始化串口对象，并获取读写流
     * path:串口路径
     * baudrate:波特率
     * flags: 标志
     */
    public void initSerialPort(String path, int baudrate, int flags) {
        try {
            if (serialPort == null) {
                if ((path.length() == 0) || (baudrate == -1)) {
                    throw new InvalidParameterException();
                }
                serialPort = new SerialPort(new File(path), baudrate, flags);
                mOutputStream = serialPort.getOutputStream();
                mInputStream = serialPort.getInputStream();

                //开启数据接收线程
                // isStop=false;
                mSerialReadThread = new SerialReadThread();//开启数据接收线程
                mSerialReadThread.start();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //定义数据接收线程
    private class SerialReadThread extends Thread {

        @Override
        public void run() {

            super.run();
            while (!isStop && !isInterrupted()) {//条件：串口未关闭 ，没有中断产生
                int index = 0, nRead = 0;
                int size = 0;
                try {
                    if (mInputStream == null) return;
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    int length =mInputStream.available();
                    byte[] data = new byte[length];

                    mInputStream.read(data,0,length);

                    if (data.length > 0){

                        //Log.i("ooo","hex = "+bytes2HexStr(data));
                        seralToDeviceHelp.onSeralToDeviceHelp(data);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private  SeralToDeviceHelp seralToDeviceHelp = null; //串口数据读取接口

    //设置数据接收接口
    public void setOnDataReceiveListener(SeralToDeviceHelp l) {
        seralToDeviceHelp = l;
    }

    private ArrayList<Byte> content = new ArrayList<Byte>();

    private String bytes2HexStr(ArrayList<Byte> src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.size() <= 0) {
            return null;
        }
        for (int i = 0; i < src.size(); i++) {
            int v = src.get(i) & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }
    private String bytes2HexStr(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    /**
     * 发送字节
     *
     * @param mBuffer
     * @return
     */
    public boolean sendBuffer(byte[] mBuffer) {
        boolean result = true;
        try {
            if (mOutputStream != null) {
                mOutputStream.write(mBuffer);
                mOutputStream.flush();
            } else {
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort() {
        if (serialPort != null) {
            serialPort.close();
            serialPort = null;
        }
    }
}

