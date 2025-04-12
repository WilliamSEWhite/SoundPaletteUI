package com.soundpaletteui.Infrastructure.ApiEndpoints;

import com.soundpaletteui.Infrastructure.Models.FileModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface FileApiEndpoints {
    @Multipart
    @POST("/api/file/upload-image")
    Call<Integer> uploadImage(
            @Part MultipartBody.Part file,
            @Part("UserId") RequestBody userId,
            @Part("FileName") RequestBody fileName,
            @Part("FileTypeId") RequestBody fileTypeId,
            @Part("FileUrl") RequestBody fileUrl
    );

    @GET("/api/file/get-profile-image/{userId}")
    Call<FileModel> getProfileImage(@Path("userId") int userId);
}
