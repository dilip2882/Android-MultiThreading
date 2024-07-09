package com.dilip.androidconcurrencywiththreads;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyTag";
    private static final String MESSAGE_KEY = "message_key";
    private ScrollView mScroll;
    private TextView mLog;
    private ProgressBar mProgressBar;
    public Handler mHandler;

    DownloadThread mDownloadThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {

                String data = msg.getData().getString(MESSAGE_KEY);

                Log.d(TAG, "handleMessage: " + data);

            }
        };


        initViews();

        mDownloadThread = new DownloadThread(MainActivity.this);
        mDownloadThread.setName("Download Thread");
        mDownloadThread.start();
    }

    public void runCode(View v) {

        log("Running code");
        displayProgressBar(true);


        //send message to download handler


        for (String song : Playlist.songs) {
            Message message = Message.obtain();
            message.obj = song;
            mDownloadThread.mHandler.sendMessage(message);

            //some changes for git demo


        }

    }

    private void initViews() {
        mScroll = findViewById(R.id.scrollLog);
        mLog = findViewById(R.id.tvLog);
        mProgressBar = findViewById(R.id.progress_bar);
    }

    public void clearOutput(View v) {
        mLog.setText("");
        scrollTextToEnd();
    }

    public void log(String message) {
        Log.i(TAG, message);
        mLog.append(message + "\n");
        scrollTextToEnd();
    }

    private void scrollTextToEnd() {
        mScroll.post(() -> mScroll.fullScroll(ScrollView.FOCUS_DOWN));
    }

    public void displayProgressBar(boolean display) {
        if (display) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

}
