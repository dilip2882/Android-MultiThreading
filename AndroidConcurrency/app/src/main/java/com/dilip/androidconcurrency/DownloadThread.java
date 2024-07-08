package com.dilip.androidconcurrency;

import android.util.Log;

public class DownloadThread extends Thread {
    private static final String TAG = "MyTag";

    @Override
    public void run() {
        for (String song : Playlist.songs) {
            downloadSong(song);
        }
    }

    void downloadSong(String songName) {
        Log.d(TAG, "run: starting download");
        try {
            Thread.sleep(4000); // Simulate download time
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Log.d(TAG, "downloadSong: " + songName + " Downloaded...");


    }
}
