package com.dilip.androidconcurrency;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MyTag";
    private ScrollView mScroll;
    private TextView mLog;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        mLog.setText(R.string.lorem_ipsum);
        displayProgressBar(false);
    }

    private void initViews() {
        mScroll = findViewById(R.id.scrollLog);
        mLog = findViewById(R.id.tvLog);
        mProgressBar = findViewById(R.id.progressBar);
    }

    public void runCode(View view) {
        log("\n\nRunning code");
        displayProgressBar(true);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: running code");
                displayProgressBar(false);
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(runnable, 4000);
    }

    private void displayProgressBar(boolean display) {
        if (display) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
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
