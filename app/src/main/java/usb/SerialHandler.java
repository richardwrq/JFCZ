package usb;

import android.os.Handler;
import android.os.Message;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/29.
 */

public  class SerialHandler extends Handler {

    private static SerialHandler INSTANCE;
    public static SerialHandler getInstance() {
        if (INSTANCE == null)
            INSTANCE = new SerialHandler();
        return INSTANCE;
    }

    ArrayList resArr = new ArrayList<Byte>();
    public void handleMessage(@NotNull Message msg) {
        super.handleMessage(msg);
        if (msg.what == 100) {
            byte[] bytes = msg.getData().getByteArray("key");
            for (int i = 0; i < bytes.length; i++) {
                resArr.add(bytes[i]);
            }
            Byte tmp1 = (Byte) this.resArr.get(this.resArr.size() - 1);
            if (tmp1 == DeviceHelp.BUFF_OVER) {
                int last = resArr.lastIndexOf(DeviceHelp.BUFF_START);
                if (last > -1) {
                    List newArr =resArr.subList(last, resArr.size());
                    byte [] newArr2 = new byte[newArr.size()];
                    for (int i = 0; i < newArr.size(); i++) {
                        newArr2[i] = (byte) resArr.get(i);
                    }
                    resArr.clear();
                    resArr = new ArrayList<Byte>();
                    DeviceHelp.getInstance().updateData(newArr2);
                }
            }
        }

    }
}