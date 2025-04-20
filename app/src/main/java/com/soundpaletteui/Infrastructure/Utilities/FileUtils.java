package com.soundpaletteui.Infrastructure.Utilities;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.soundpaletteui.Infrastructure.Models.FileModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileUtils {

    private static final Date now = new Date();

    /** returns the filename based on the date and user Id */
    private static String nameImageFile(int userId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String formattedDate = dateFormat.format(now);
        return userId + "-" + formattedDate + ".jpg";
    }

    /** returns the filename based on the date and user Id */
    private static String nameSoundFile(int userId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String formattedDate = dateFormat.format(now);
        return userId + "-" + formattedDate + ".mp3";
    }

    public static File uri2File(Context context, Uri uri, int fileId, int userId) {
        String fileName = null;
        try{
            ContentResolver contentResolver = context.getContentResolver();
            switch(fileId) {
                case 1:
                    fileName = "null.jpg";
                    break;
                case 2:
                    fileName = nameSoundFile(userId);
                    break;
                case 3:
                    fileName = nameImageFile(userId);
                    break;
                case 4:
                    fileName = nameImageFile(userId);
                    break;
            }
            File tempFile = new File(context.getCacheDir(), fileName);
            try(InputStream inputStream = contentResolver.openInputStream(uri);
                FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int len;
                while((len = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }
            }
            return tempFile;
        }
        catch(Exception e) {
            Log.e("FileUtils", "Failed to convert and rename file");
            return null;
        }
    }

    public static void getPostAudio(int fileId, Call<FileModel> fileCall, Context context, ImageButton playButton, SeekBar seekBar) {
        fileCall.enqueue(new Callback<FileModel>() {
            @Override
            public void onResponse(Call<FileModel> call, Response<FileModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Get the base64-encoded content of the audio file
                    String base64Audio = response.body().getByteArrayContent();

                    try {
                        // Decode the base64 audio data
                        byte[] decodedAudio = Base64.decode(base64Audio, Base64.DEFAULT);

                        // Create a file in the cache directory
                        File cacheDir = context.getCacheDir();
                        File audioFile = new File(cacheDir, "downloaded_audio.mp3");

                        // Write the decoded audio data to the file
                        try (FileOutputStream fos = new FileOutputStream(audioFile)) {
                            fos.write(decodedAudio);
                            fos.flush();

                            // Get the audio file URL (this can be a local URI since we saved the file)
                            Uri audioUri = Uri.fromFile(audioFile);

                            // Use MediaPlayerManager to handle playback
                            MediaPlayerManager.getInstance().playPause(audioUri.toString(), playButton, seekBar);

                        } catch (IOException e) {
                            Log.e("getPostAudio", "Error writing audio to file", e);
                            Toast.makeText(context, "Audio download failed", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Log.e("getPostAudio", "Error decoding audio", e);
                        Toast.makeText(context, "Audio decoding failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("getPostAudio", "Error: " + response.message());
                    Toast.makeText(context, "Failed to download audio", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FileModel> call, Throwable t) {
                Log.e("getPostAudio", "Error downloading audio", t);
                Toast.makeText(context, "Error downloading audio", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
