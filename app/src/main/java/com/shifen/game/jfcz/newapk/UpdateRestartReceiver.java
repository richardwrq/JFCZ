package com.shifen.game.jfcz.newapk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.shifen.game.jfcz.ui.ADActivity;

/**
 * Created by Administrator on 2018/10/30.
 */

public class UpdateRestartReceiver extends BroadcastReceiver
{
    private static final String TAG = "Download";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")){
            Log.i(TAG, "onReceive: 已升级到新版本");
            Intent intent2 = new Intent(context, ADActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);

        }
    }

}
