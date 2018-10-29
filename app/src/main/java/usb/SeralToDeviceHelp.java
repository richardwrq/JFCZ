package usb;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Administrator on 2018/10/28.
 */

public interface SeralToDeviceHelp {
    void onSeralToDeviceHelp(@NotNull byte[] msg); //在接收函数中解析收到的数据
}
