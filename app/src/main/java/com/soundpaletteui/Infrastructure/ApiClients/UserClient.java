package com.soundpaletteui.Infrastructure.ApiClients;

import com.soundpaletteui.Infrastructure.ApiEndpoints.LoginRegisterApiEndpoints;
import com.soundpaletteui.Infrastructure.ApiEndpoints.UserEndpoints;
import com.soundpaletteui.Infrastructure.Models.UserInfoModel;
import com.soundpaletteui.Infrastructure.Models.UserModel;
import com.soundpaletteui.Infrastructure.Models.UserProfileModel;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;

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

    public UserInfoModel getUserInfo(int id) throws IOException {
        Call<UserInfoModel> call = userEndpoints.getUserInfo(id);
        Response<UserInfoModel> response = call.execute();
        return response.body();
    }

    public UserProfileModel getUserProfile(int id) throws IOException {
        Call<UserProfileModel> call = userEndpoints.getUserProfile(id);
        Response<UserProfileModel> response = call.execute();
        return response.body();
    }
    public UserProfileModel getUserProfileByUsername(String username) throws IOException {
        Call<UserProfileModel> call = userEndpoints.getUserProfileByUsername(username);
        Response<UserProfileModel> response = call.execute();
        return response.body();
    }
    public UserProfileModel updateUserProfile(UserProfileModel userProfile) throws IOException {
        Call<UserProfileModel> call = userEndpoints.updateUserProfile(userProfile);
        Response<UserProfileModel> response = call.execute();
        return response.body();
    }
    public Void followUser(String followingUsername) throws IOException {
        int userId = AppSettings.getInstance().getUserId();

        Call<Void> call = userEndpoints.followUser(userId, followingUsername);
        Response<Void> response = call.execute();

        return response.body();
    }
    public Void unfollowUser(String followingUsername) throws IOException {
        int userId = AppSettings.getInstance().getUserId();

        Call<Void> call = userEndpoints.unfollowUser(userId, followingUsername);
        Response<Void> response = call.execute();

        return response.body();
    }
}
