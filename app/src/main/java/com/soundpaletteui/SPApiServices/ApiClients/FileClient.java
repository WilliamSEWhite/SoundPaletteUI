package com.soundpaletteui.SPApiServices.ApiClients;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.soundpaletteui.SPApiServices.ApiEndpoints.FileApiEndpoints;
import com.soundpaletteui.Infrastructure.Models.FileModel;
import com.soundpaletteui.Infrastructure.Utilities.FileUtils;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// Handles API calls for file operations (e.g., uploading images, retrieving profile and post files) using Retrofit.
public class FileClient {

    private static FileApiEndpoints fileApiEndpoints;

    public FileClient(Retrofit retrofit) {
        fileApiEndpoints = retrofit.create(FileApiEndpoints.class);
    }
    public FileClient(Retrofit retrofit, Context context) {
        fileApiEndpoints = retrofit.create(FileApiEndpoints.class);
    }

    // Upload profile image to API server
    public Call<Integer> uploadImage(File file, int userId, int fileTypeId, String fileUrl) {
        // Wrap for text fields
        RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId));
        RequestBody fileNameBody = RequestBody.create(MediaType.parse("text/plain"), file.getName());
        RequestBody fileTypeIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(fileTypeId));
        RequestBody fileUrlBody = RequestBody.create(MediaType.parse("text/plain"), fileUrl);

        // Wrap file part
        RequestBody fileRequestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), fileRequestBody);

        // API call
        return fileApiEndpoints.uploadImage(filePart, userIdBody, fileNameBody, fileTypeIdBody, fileUrlBody);
    }

    // Get profile image from API server
    public void getProfileImage(int userId, Callback<FileModel> callback) {
        Call<FileModel> call = fileApiEndpoints.getProfileImage(userId);
        call.enqueue(callback);
    }
    // Get profile image from API server
    public Call<FileModel> getProfileImage(int userId) {
        return fileApiEndpoints.getProfileImage(userId);
    }

    // Get post file from API server
    public Call<FileModel> getPostFile(int fileId) {
        return fileApiEndpoints.getPostFile(fileId);
    }
}
