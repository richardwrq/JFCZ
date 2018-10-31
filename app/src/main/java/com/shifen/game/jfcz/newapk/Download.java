package com.shifen.game.jfcz.newapk;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/10/31.
 */

public class Download {

    private static final String TAG = "Download";

    private static Download INSTANCE;
    public static Download getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Download();
        return INSTANCE;
    }


    public void start(String url){
        final String apkName=getNameFromUrl(url);
        Download.getInstance().download(url, new DownloadListener() {
            @Override
            public void start(long max) {
                Log.i(TAG,apkName+" start " +max);
            }

            @Override
            public void loading(int progress) {
                Log.i(TAG,apkName+" loading "+progress);
            }

            @Override
            public void complete(String path) {
                Log.i(TAG,apkName+ " complete "+path);
            }

            @Override
            public void fail(int code, String message) {
                Log.i(TAG,apkName+" fail "+message);
            }

            @Override
            public void loadfail(String message) {
                Log.i(TAG,apkName+" fail "+message);
            }

            @Override
            public void installfail(String message) {
                Log.i(TAG,apkName+" installfail "+message);
            }
        });

    }

    public void download(final String url, final DownloadListener downloadListener) {
        final Long startsPoint = getFileStart(url) > 0 ? getFileStart(url)-1 : getFileStart(url);
        Request request = new Request.Builder().url(url).header("RANGE", "bytes=" + startsPoint + "-").build();
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder().body(new DownloadResponseBody(originalResponse, startsPoint, downloadListener)).build();
            }
        };
        OkHttpClient.Builder dlOkhttp = new OkHttpClient.Builder().addNetworkInterceptor(interceptor);
        dlOkhttp.readTimeout(5, TimeUnit.MINUTES);

        // 发起请求
        dlOkhttp.build().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "发起请求 onFailure: "+e);
                downloadListener.loadfail(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {


                long length = response.body().contentLength();
                if (length == 0) { // 说明文件已经下载完，直接跳转安装就好
                    downloadListener.complete(String.valueOf(getFile(url).getAbsoluteFile()));
                    return;
                }
                downloadListener.start(length + startsPoint); // 保存文件到本地
                InputStream is = null;
                RandomAccessFile randomAccessFile = null;
                BufferedInputStream bis = null;
                Log.i(TAG, "发起请求 onResponse: ");
                byte[] buff = new byte[2048];
                int len = 0;
                try {
                    is = response.body().byteStream();
                    bis = new BufferedInputStream(is);
                    File file = getFile(url); // 随机访问文件，可以指定断点续传的起始位置
                    randomAccessFile = new RandomAccessFile(file, "rwd");
                    randomAccessFile.seek(startsPoint);
                    while ((len = bis.read(buff)) != -1) {
                        randomAccessFile.write(buff, 0, len);
                    } // 下载完成
                    String apkPath = String.valueOf(file.getAbsoluteFile());
                    downloadListener.complete(apkPath);
                    install(apkPath,downloadListener);

                } catch (Exception e) {
                    e.printStackTrace();
                    downloadListener.loadfail(e.getMessage());
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (bis != null) {
                            bis.close();
                        }
                        if (randomAccessFile != null) {
                            randomAccessFile.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }


    //打开APK程序代码
    public void install(String apkPath, DownloadListener downloadListener) {
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        try {
            // 申请su权限
            Process process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            // 执行pm install命令
            String command = "pm install -r " + apkPath + "\n";
            Log.i(TAG, "install:command= " + command);
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String msg = "";
            String line;
            // 读取命令的执行结果
            while ((line = errorStream.readLine()) != null) {
                msg += line;
            }
            Log.d(TAG, "install msg is " + msg);
            // 如果执行结果中包含Failure字样就认为是安装失败，否则就认为安装成功
            if (msg.contains("Failure")) {
                downloadListener.installfail("执行结果中包含Failure");
            }
        } catch (Exception e) {
            downloadListener.installfail(e.getMessage());
            Log.e(TAG, e.getMessage(), e);
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    private String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }


    private File getFile(String url) {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(root, getNameFromUrl(url));
        return file;
    }

    private long getFileStart(String url) {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(root,  getNameFromUrl(url));
        return file.length();
    }


}
