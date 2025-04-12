package com.soundpaletteui.Infrastructure.Utilities;

import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.soundpaletteui.R;

import java.io.IOException;

public class MediaPlayerManager {
    private static MediaPlayerManager instance;
    private MediaPlayer mediaPlayer;
    private String currentlyPlayingUrl = null;
    private ImageButton currentPlayButton = null;
    private SeekBar currentSeekBar = null;
    private Handler handler = new Handler();

    private MediaPlayerManager() {}

    public static synchronized MediaPlayerManager getInstance() {
        if (instance == null) {
            instance = new MediaPlayerManager();
        }
        return instance;
    }

    public void playPause(String url, ImageButton playButton, SeekBar seekBar) {
        if (mediaPlayer != null && url.equals(currentlyPlayingUrl)) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playButton.setImageResource(R.drawable.baseline_play_circle_filled_24);
                return;
            } else {
                mediaPlayer.start();
                playButton.setImageResource(R.drawable.baseline_pause_circle_filled_24);
                handler.post(updateSeekBar(seekBar));
                return;
            }
        }

        // Stop current
        release();

        mediaPlayer = new MediaPlayer();
        currentlyPlayingUrl = url;
        currentPlayButton = playButton;
        currentSeekBar = seekBar;

        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                playButton.setImageResource(R.drawable.baseline_pause_circle_filled_24);
                handler.post(updateSeekBar(seekBar));
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                playButton.setImageResource(R.drawable.baseline_play_circle_filled_24);
                seekBar.setProgress(0);
                currentlyPlayingUrl = null;
                handler.removeCallbacksAndMessages(null);
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e("MediaPlayerManager", "Error playing audio", e);
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (Exception ignored) {}
            mediaPlayer.release();
            mediaPlayer = null;
        }
        currentlyPlayingUrl = null;
        handler.removeCallbacksAndMessages(null);
        if (currentPlayButton != null) {
            currentPlayButton.setImageResource(R.drawable.baseline_play_circle_filled_24);
        }
    }

    private Runnable updateSeekBar(SeekBar seekBar) {
        return new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int progress = (int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100);
                    seekBar.setProgress(progress);
                    handler.postDelayed(this, 100);
                }
            }
        };
    }

    public void seekToPercent(int percent) {
        if (mediaPlayer != null) {
            int newPosition = (mediaPlayer.getDuration() * percent) / 100;
            mediaPlayer.seekTo(newPosition);
        }
    }
}
