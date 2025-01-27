package com.soundpaletteui.Main.Activities.apiTest.interfaces;

import com.soundpaletteui.Main.Activities.apiTest.models.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LoginApi {

    @GET("api/login/login-user")
    Call<User> loginUser(
            @Query("username") String username,
            @Query("password") String password
    );
}
