package com.dilip.androidconcurrency;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MyTag";
    private ScrollView mScroll;
    private TextView mLog;
    private ProgressBar mProgressBar;
    private Animation mProgressBarAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        mScroll = findViewById(R.id.scrollLog);
        mLog = findViewById(R.id.tvLog);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBarAnimation = AnimationUtils.loadAnimation(this, R.anim.progress_bar_animation);
    }

    public void runCode(View view) {
        log("\nRunning code");

        // Show the progress bar
        displayProgressBar(true);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: starting download");
                try {
                    Thread.sleep(4000); // Simulate download time
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Log.d(TAG, Thread.currentThread().getName());
                Log.d(TAG, "run: download completed");

                // Hide the progress bar after download completes
                displayProgressBar(false);
            }
        };

        Thread thread = new Thread(runnable);
        thread.setName("Download Thread");
        thread.start();
    }

    private void displayProgressBar(boolean display) {
        if (display) {
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.startAnimation(mProgressBarAnimation);
        } else {
            mProgressBar.clearAnimation();
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void log(String message) {
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
}
