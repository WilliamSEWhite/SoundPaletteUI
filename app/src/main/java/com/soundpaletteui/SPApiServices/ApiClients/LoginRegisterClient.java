package com.soundpaletteui.SPApiServices.ApiClients;

import com.soundpaletteui.SPApiServices.ApiEndpoints.LoginRegisterApiEndpoints;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginRegisterClient {
    private static LoginRegisterApiEndpoints loginRegisterApiEndpoint;

    public LoginRegisterClient(Retrofit retrofit) {
        loginRegisterApiEndpoint = retrofit.create(LoginRegisterApiEndpoints.class);
    }

    public UserModel loginUser(String username, String password) throws IOException {
        Call<UserModel> call = loginRegisterApiEndpoint.loginUser(username, password);
        Response<UserModel> response = call.execute();
        return response.body();
    }
    public UserModel registerUser(String username, String password) throws IOException {
        Call<UserModel> call = loginRegisterApiEndpoint.registerUser(username, password);
        Response<UserModel> response = call.execute();
        return response.body();
    }
}