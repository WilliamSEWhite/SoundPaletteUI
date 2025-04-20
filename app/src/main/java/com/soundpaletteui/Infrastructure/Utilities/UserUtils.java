package com.soundpaletteui.Infrastructure.Utilities;

import android.util.Log;

import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.SPApiServices.ApiClients.UserClient;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserUtils {

    private static UserClient client = SPWebApiRepository.getInstance().getUserClient();;
    private static UserModel user;

    /** callback interface */
    public interface UserIdCallback {
        void onUserIdReceived(int userId);
        void onError(Throwable t);
    }

    /** returns the userId based on the username */
    public static int getUserId(String username, UserIdCallback callback) {
        final int[] userId = new int[1];
        client.getUserByName(username, new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int userId = response.body().getUserId();
                    Log.d("UserUtils", "UserId: " + userId);
                    callback.onUserIdReceived(userId);
                } else {
                    callback.onError(new Exception("Failed to get user ID"));
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                Log.e("UserUtils", "Error retrieving UserModel", t);
                callback.onError(t);
            }
        });
        return userId[0];
    }

}
