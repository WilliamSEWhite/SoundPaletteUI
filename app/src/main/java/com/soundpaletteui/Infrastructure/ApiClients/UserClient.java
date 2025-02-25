package com.soundpaletteui.Infrastructure.ApiClients;

import com.soundpaletteui.Infrastructure.ApiEndpoints.LoginRegisterApiEndpoints;
import com.soundpaletteui.Infrastructure.ApiEndpoints.UserEndpoints;
import com.soundpaletteui.Infrastructure.Models.UserInfoModel;
import com.soundpaletteui.Infrastructure.Models.UserModel;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserClient {
    private static UserEndpoints userEndpoints;

    public UserClient(Retrofit retrofit) {
        userEndpoints = retrofit.create(UserEndpoints.class);
    }
    public UserModel getUser(int id) throws IOException {
        Call<UserModel> call = userEndpoints.getUser(id);
        Response<UserModel> response = call.execute();
        return response.body();
    }

    public UserModel updateUserInfo(int id, UserInfoModel userInfo) throws IOException {
        Call<UserModel> call = userEndpoints.updateUserInfo(id, userInfo);
        Response<UserModel> response = call.execute();
        return response.body();
    }

    /*public UserInfoModel getUserInfo(int id) {
        Call<UserInfoModel> call = userEndpoints.getUser(id);
        Response<UserInfoModel> response = call.execute();
        return response.body();
    }*/
}
