package com.soundpaletteui.SPApiServices.ApiClients;

import android.util.Log;

import com.google.firebase.firestore.auth.User;
import com.soundpaletteui.Infrastructure.Models.User.UserSearchModel;
import com.soundpaletteui.SPApiServices.ApiEndpoints.UserEndpoints;
import com.soundpaletteui.Infrastructure.Models.User.UserInfoModel;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.Infrastructure.Models.User.UserProfileModel;
import com.soundpaletteui.Infrastructure.Models.User.UserProfileModelLite;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
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

    public void getUserByName(String userName, Callback<UserModel> callback) {
        Call<UserModel> call = userEndpoints.getUserByName(userName);
        call.enqueue(callback);
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
    public UserProfileModelLite getUserProfileByUsername(String username) throws IOException {
        int userId = AppSettings.getInstance().getUserId();
        Call<UserProfileModelLite> call = userEndpoints.getUserProfileByUsername(username, userId);
        Response<UserProfileModelLite> response = call.execute();
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

    public List<String> searchUsersLite(String searchTerm) throws IOException {
        int userId = AppSettings.getInstance().getUserId();

        Call<List<String>> call = userEndpoints.searchUsersLite(userId, searchTerm);
        Response<List<String>> response = call.execute();

        if (!response.isSuccessful()) {
            Log.e("PostClient", "Error fetching users: " + response.code() + " - " + response.message());
            return new ArrayList<>();
        }

        List<String> users = response.body();
        if (users == null) {
            Log.w("UserClient", "Received null response body for users.");
            return new ArrayList<>();
        }

        Log.d("UserClient", "Fetched " + users.size() + " user.");
        return users;
    }


    public List<UserSearchModel> searchUsers(String searchTerm) throws IOException {
        int userId = AppSettings.getInstance().getUserId();

        Call<List<UserSearchModel>> call = userEndpoints.searchUsers(userId, searchTerm);
        Response<List<UserSearchModel>> response = call.execute();

        if (!response.isSuccessful()) {
            Log.e("PostClient", "Error fetching users: " + response.code() + " - " + response.message());
            return new ArrayList<>();
        }

        List<UserSearchModel> users = response.body();
        if (users == null) {
            Log.w("UserClient", "Received null response body for users.");
            return new ArrayList<>();
        }

        Log.d("UserClient", "Fetched " + users.size() + " user.");
        return users;
    }

}
