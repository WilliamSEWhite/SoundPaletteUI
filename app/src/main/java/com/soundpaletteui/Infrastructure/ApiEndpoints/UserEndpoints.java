package com.soundpaletteui.Infrastructure.ApiEndpoints;

import com.soundpaletteui.Infrastructure.Models.UserInfoModel;
import com.soundpaletteui.Infrastructure.Models.UserModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserEndpoints {
    @GET("api/user/get-user/{id}")
    Call<UserModel> getUser(@Path("id") int id);
    @POST("api/user/update-user-info")
    Call<UserInfoModel> updateUserInfo(@Body UserInfoModel userInfo);
}
