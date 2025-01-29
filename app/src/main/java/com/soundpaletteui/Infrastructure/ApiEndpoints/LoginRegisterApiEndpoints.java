package com.soundpaletteui.Infrastructure.ApiEndpoints;

import com.soundpaletteui.Infrastructure.Models.UserModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LoginRegisterApiEndpoints {
        @GET("api/login/login-user")
        Call<UserModel> loginUser(@Query("username") String username, @Query("password") String password);
        @GET("api/login/register")
        Call<UserModel> registerUser(@Query("username") String username, @Query("password") String password);



}
