package com.shifen.game.jfcz.restart;
import java.lang.Thread.UncaughtExceptionHandler;  
import android.content.Context; 
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;  
 
public class CrashHandler implements UncaughtExceptionHandler {
    public final String TAG = CrashHandler.class.getName();
    private static CrashHandler INSTANCE;
    private Context mContext; 
    private UncaughtExceptionHandler mDefaultHandler;
    
     
    private CrashHandler() {  
    }  
  
    public static CrashHandler getInstance() {  
        if (INSTANCE == null)  
            INSTANCE = new CrashHandler();  
        return INSTANCE;  
    }  
      
    public void init(Context ctx) {  
        mContext = ctx;  
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();  
        Thread.setDefaultUncaughtExceptionHandler(this);  
    }  
      
    @Override  
    public void uncaughtException(Thread thread, Throwable ex) {  
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);  
        } else {
            try {  
                Thread.sleep(3000);  
            } catch (InterruptedException e) {  
            	Log.e(TAG,"Error : ", e);  
            }
            RestartAPPTool.restartAPP(mContext,2000);
            //android.os.Process.killProcess(android.os.Process.myPid());
            //System.exit(10);
        }  
    }  
  
    private boolean handleException(Throwable ex) {  
        if (ex == null) {  
            return true;  
        }
        Log.e(TAG, "",ex);
        final String msg = ex.getLocalizedMessage();  
        new Thread() {  
            @Override  
            public void run() {
                Looper.prepare();  
                Toast.makeText(mContext,  msg, Toast.LENGTH_LONG).show();
                //  RestartAPPTool.restartAPP(mContext,2000);
                Looper.loop();  
            }  
        }.start();  
        return true;  
    }  
  
}  