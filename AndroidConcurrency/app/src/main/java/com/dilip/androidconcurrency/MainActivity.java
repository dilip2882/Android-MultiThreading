package com.dilip.androidconcurrency;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MyTag";
    private static final String MESSAGE_KEY = "message_key";
    private ScrollView mScroll;
    private TextView mLog;
    private ProgressBar mProgressBar;
    private Animation mProgressBarAnimation;
    private Handler mHandler;
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

    private void initViews() {
        mScroll = findViewById(R.id.scrollLog);
        mLog = findViewById(R.id.tvLog);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBarAnimation = AnimationUtils.loadAnimation(this, R.anim.progress_bar_animation);
    }

    public void runCode(View view) {

        log("\nRunning code");
        displayProgressBar(true); // Show the progress bar

        // send message or runnable to download handler

        for (String song : Playlist.songs) {
            Message message = Message.obtain();
            message.obj = song;
            mDownloadThread.mHandler.sendMessage(message);
        }

        MyTask myTask = new MyTask();
        myTask.execute("Red","Green","Blue","Yellow");

        MyTask myTask2 = new MyTask();
        myTask2.execute("Black","White");
    }

    public void displayProgressBar(boolean display) {
        if (display) {
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.startAnimation(mProgressBarAnimation);
        } else {
            mProgressBar.clearAnimation();
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void log(String message) {
        Log.i(TAG, message);
        mLog.append(message + "\n");
        scrollTextToEnd();
    }

    public void clearOutput(View view) {
        mLog.setText("");
        scrollTextToEnd();
    }

    private void scrollTextToEnd() {
        mScroll.post(new Runnable() {
            @Override
            public void run() {
                mScroll.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    class MyTask extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {

            for (String value: strings) {
                Log.d(TAG, "doInBackground: " + value);
                publishProgress(value);

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
            return "onPostExecute: Download Completed";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            log(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            log(s);

        }
    }
}
