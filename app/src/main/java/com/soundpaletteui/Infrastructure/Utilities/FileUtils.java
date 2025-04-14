package com.soundpaletteui.Infrastructure.Utilities;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.text.SimpleDateFormat;

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
                    fileName = nameImageFile(userId);
                    break;
                case 2:
                    fileName = nameSoundFile(userId);
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
}
