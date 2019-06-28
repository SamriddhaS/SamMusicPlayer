package com.example.samriddha.sammusicapp.helpers;


import android.media.MediaPlayer;

/**
 * Created by Samriddha on 4/02/2019.
 */

public class Listeners {

    public interface LoadSongListener {
        void onSongLoaded();
    }

    public interface LoadImageListener {
        void onImageLoaded();
    }

    public interface MediaPlayerListener {
        void onMediaPlayerStarted(MediaPlayer mp);
    }
}
