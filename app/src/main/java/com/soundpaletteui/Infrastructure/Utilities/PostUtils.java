package com.soundpaletteui.Infrastructure.Utilities;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.soundpaletteui.Infrastructure.Models.FileModel;
import com.soundpaletteui.Infrastructure.Models.Post.NewPostModel;
import com.soundpaletteui.Infrastructure.Models.Post.PostFileModel;
import com.soundpaletteui.SPApiServices.ApiClients.PostClient;
import com.soundpaletteui.SPApiServices.ApiEndpoints.PostApiEndpoints;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

// Handles utilities related to creating and uploading posts
public class PostUtils {
    private static final Gson gson = new GsonBuilder()
            .setLenient()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create();
    private static final PostClient postClient = SPWebApiRepository.getInstance().getPostClient();

    // Uploads a file-based post (image or audio) to the server
    public static void uploadFilePost(File file,
                                      NewPostModel postModel,
                                      FileModel fileModel,
                                      Callback<Integer> callback) {
        // prepare the file part
        RequestBody fileBody;
        MultipartBody.Part filePart = null;

        // Check if image or audio file
        switch(postModel.postTypeId) {
            case 2:         // Handle image file
                fileBody = RequestBody.create(file, MediaType.parse("image/*"));
                filePart = MultipartBody.Part.createFormData("File", file.getName(), fileBody);
                break;
            case 3:         // Handle audio file
                fileBody = RequestBody.create(file, MediaType.parse("audio/*"));
                filePart = MultipartBody.Part.createFormData("File", file.getName(), fileBody);
                break;
            default:
                // Log if the post type is not supported
                Log.d("PostUtils", "Neither image nor sound file");
                break;
        }

        // Combine post and file models with wrapper class
        PostFileModel combinedModel = new PostFileModel(postModel, fileModel);
        String json = gson.toJson(combinedModel);
        RequestBody metaDataJson = RequestBody.create(json, MediaType.parse("text/plain"));

        // API call
        postClient.createFilePost(filePart, metaDataJson, callback);
    }
}
