package com.soundpaletteui.Infrastructure.ApiClients;

import com.soundpaletteui.Infrastructure.ApiEndpoints.LoginRegisterApiEndpoint;
import com.soundpaletteui.Infrastructure.Models.UserModel;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginRegisterClient {
    private static LoginRegisterApiEndpoint loginRegisterApiEndpoint;

    public LoginRegisterClient(Retrofit retrofit) {
        loginRegisterApiEndpoint = retrofit.create(LoginRegisterApiEndpoint.class);
    }

    public UserModel loginUser(String username, String password) throws IOException {
        Call<UserModel> call = loginRegisterApiEndpoint.loginUser(username, password);
        Response<UserModel> response = call.execute();
        return response.body();
    }
    public Object login() throws IOException {
        Call<Object> call = loginRegisterApiEndpoint.login();
        Response<Object> response = call.execute();
        return response.body();
    }
}