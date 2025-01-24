package com.soundpaletteui.Infrastructure.ApiEndpoints;

import com.soundpaletteui.Infrastructure.Models.UserModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LoginRegisterApiEndpoint {
        @GET("api/login/login_user")
        Call<UserModel> loginUser(@Query("username") String user, @Query("password") String password);
        @GET("api/login")
        Call<Object> login();

}
