package com.soundpaletteui.Infrastructure.Utilities;

import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.soundpaletteui.R;

import java.io.IOException;

// Handles audio playback and media player controls
public class MediaPlayerManager {
    private static MediaPlayerManager instance;
    private MediaPlayer mediaPlayer;
    private String currentlyPlayingUrl = null;
    private ImageButton currentPlayButton = null;
    private SeekBar currentSeekBar = null;
    private Handler handler = new Handler();

    // Private constructor for singleton pattern
    private MediaPlayerManager() {}

    // Returns the shared instance of MediaPlayerManager
    public static synchronized MediaPlayerManager getInstance() {
        if (instance == null) {
            instance = new MediaPlayerManager();
        }
        return instance;
    }

    // Plays or pauses audio based on the current state
    public void playPause(String url, ImageButton playButton, SeekBar seekBar) {
        if (mediaPlayer != null && url.equals(currentlyPlayingUrl)) {
            if (mediaPlayer.isPlaying()) {
                // Pause the current audio
                mediaPlayer.pause();
                playButton.setImageResource(R.drawable.baseline_play_circle_filled_24);
                return;
            } else {
                // Resume the current audio
                mediaPlayer.start();
                playButton.setImageResource(R.drawable.baseline_pause_circle_filled_24);
                handler.post(updateSeekBar(seekBar));
                return;
            }
        }

        // Stop and release the current media player if a new audio is selected
        release();

        // Create a new MediaPlayer instance
        mediaPlayer = new MediaPlayer();
        currentlyPlayingUrl = url;
        currentPlayButton = playButton;
        currentSeekBar = seekBar;

        try {
            mediaPlayer.setDataSource(url);

            // Start playback when ready
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                playButton.setImageResource(R.drawable.baseline_pause_circle_filled_24);
                handler.post(updateSeekBar(seekBar));
            });

            // Reset button and seekbar when playback completes
            mediaPlayer.setOnCompletionListener(mp -> {
                playButton.setImageResource(R.drawable.baseline_play_circle_filled_24);
                seekBar.setProgress(0);
                currentlyPlayingUrl = null;
                handler.removeCallbacksAndMessages(null);
            });

            // Prepare asynchronously
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e("MediaPlayerManager", "Error playing audio", e);
        }
    }

    // Releases the current MediaPlayer instance
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

        // Reset play button if one exists
        if (currentPlayButton != null) {
            currentPlayButton.setImageResource(R.drawable.baseline_play_circle_filled_24);
        }
    }

    // Updates the seekbar while audio is playing
    private Runnable updateSeekBar(SeekBar seekBar) {
        return new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int progress = (int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100);
                    seekBar.setProgress(progress);
                    handler.postDelayed(this, 100); // Update every 100 milliseconds
                }
            }
        };
    }

    // Moves playback to a new position based on percent
    public void seekToPercent(int percent) {
        if (mediaPlayer != null) {
            int newPosition = (mediaPlayer.getDuration() * percent) / 100;
            mediaPlayer.seekTo(newPosition);
        }
    }
}
