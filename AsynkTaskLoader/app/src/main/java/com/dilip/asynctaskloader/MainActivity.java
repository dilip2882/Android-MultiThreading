package com.dilip.asynctaskloader;

import static com.dilip.asynctaskloader.Playlist.songs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private static final String TAG = "MyTag";
    private static final String MESSAGE_KEY = "message_key";
    private static final String DATA_KEY = "data_key";
    private ScrollView mScroll;
    private TextView mLog;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void runCode(View v) {

        Bundle bundle = new Bundle();
        bundle.putString(DATA_KEY, "some url that returns some data");

        getSupportLoaderManager().restartLoader(1000, bundle, this).forceLoad();

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

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {

        List<String> songList = Arrays.asList(songs);

        return new MyTaskLoader(this, args, songList);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        log(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    private static class MyTaskLoader extends AsyncTaskLoader<String> {

        private final Bundle mArgs;
        private final List<String> mSongsList;

        public MyTaskLoader(@NonNull Context context, Bundle args, List<String> songList) {
            super(context);
            this.mArgs = args;
            this.mSongsList = songList;
        }

        @Nullable
        @Override
        public String loadInBackground() {

            String data = mArgs.getString(DATA_KEY);

            Log.d(TAG, "loadInBackground: URL: " + data);
            Log.d(TAG, "loadInBackground: Thread Name" + Thread.currentThread().getName());

            for (String song : mSongsList) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Log.d(TAG, "loadInBackground: Song Name: "+ song);
            }

            Log.d(TAG, "loadInBackground: Thread Terminated");

            return "result from loader";
        }

        @Override
        public void deliverResult(@Nullable String data) {
            data += ": modified";

            super.deliverResult(data);
        }
    }

}
