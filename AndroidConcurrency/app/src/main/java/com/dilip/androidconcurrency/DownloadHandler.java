package com.dilip.androidconcurrency;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

public class DownloadHandler extends Handler {

    private static final String TAG = "MyTag";
    private final MainActivity mActivity;

    public DownloadHandler(MainActivity activity) {
        this.mActivity = activity;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        downloadSong(msg.obj.toString());
    }

    void downloadSong(final String songName) {
        Log.d(TAG, "run: starting download");
        try {
            Thread.sleep(4000); // Simulate download time
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivity.log("Download Complete " + songName);
                mActivity.displayProgressBar(false);
            }
        });

//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                mActivity.log("Download Complete");
//                mActivity.displayProgressBar(false);
//            }
//        });

        Log.d(TAG, "downloadSong: "+songName+" Downloaded...");

    }
}

