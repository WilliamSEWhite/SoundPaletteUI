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
    public UserInfoModel updateUserInfo(UserInfoModel userInfo) throws IOException {
        Call<UserInfoModel> call = userEndpoints.updateUserInfo(userInfo);
        Response<UserInfoModel> response = call.execute();
        return response.body();
    }
}
