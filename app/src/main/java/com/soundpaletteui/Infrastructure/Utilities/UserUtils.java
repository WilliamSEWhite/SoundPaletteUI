package com.soundpaletteui.Infrastructure.Utilities;

import android.util.Log;

import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.SPApiServices.ApiClients.UserClient;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Provides helper methods for retrieving user information
public class UserUtils {
    private static UserClient client = SPWebApiRepository.getInstance().getUserClient();;
    private static UserModel user;

    // Callback interface for returning a userId asynchronously
    public interface UserIdCallback {
        void onUserIdReceived(int userId);
        void onError(Throwable t);
    }

    // Returns the userId based on the username
    public static int getUserId(String username, UserIdCallback callback) {
        final int[] userId = new int[1];

        // Fetch the user by username using the API
        client.getUserByName(username, new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                // If successfully retrieved user, pass back the userId
                if (response.isSuccessful() && response.body() != null) {
                    int userId = response.body().getUserId();
                    Log.d("UserUtils", "UserId: " + userId);
                    callback.onUserIdReceived(userId);
                // else API response error
                } else {
                    callback.onError(new Exception("Failed to get user ID"));
                }
            }

            @Override
            // Network or server error
            public void onFailure(Call<UserModel> call, Throwable t) {
                Log.e("UserUtils", "Error retrieving UserModel", t);
                callback.onError(t);
            }
        });
        return userId[0];
    }

}
