package com.dilip.androidconcurrency;

import android.os.Looper;

public class DownloadThread extends Thread {
    private static final String TAG = "MyTag";
    private final MainActivity mActvity;
    public DownloadHandler mHandler;

    public DownloadThread(MainActivity activity) {
        this.mActvity = activity;
    }

    @Override
    public void run() {

        Looper.prepare();
        mHandler = new DownloadHandler(mActvity);
        Looper.loop();
    }

}
