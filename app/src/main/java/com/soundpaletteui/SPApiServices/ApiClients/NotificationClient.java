package com.soundpaletteui.SPApiServices.ApiClients;

import android.util.Log;

import com.soundpaletteui.Infrastructure.Models.Chat.ChatroomModelLite;
import com.soundpaletteui.Infrastructure.Models.Chat.NewChatroomModel;
import com.soundpaletteui.Infrastructure.Models.Notifications.NotificationModel;
import com.soundpaletteui.Infrastructure.Models.Notifications.NotificationSettingModel;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.SPApiServices.ApiEndpoints.NotificationApiEndpoints;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NotificationClient {
    private NotificationApiEndpoints apiEndpoints;

    public NotificationClient(Retrofit retrofit){
        apiEndpoints = retrofit.create(NotificationApiEndpoints.class);

    }
    public List<NotificationModel> getNotifications() throws IOException {
        int userId = AppSettings.getInstance().getUserId();
        Call<List<NotificationModel>> call = apiEndpoints.getNotifications(userId);
        Response<List<NotificationModel>> response = call.execute();

        if (!response.isSuccessful()) {
            Log.e("PostClient", "Error fetching posts: " + response.code() + " - " + response.message());
            return new ArrayList<>();
        }

        List<NotificationModel> posts = response.body();
        if (posts == null) {
            Log.w("PostClient", "Received null response body for posts.");
            return new ArrayList<>();
        }

        Log.d("PostClient", "Fetched " + posts.size() + " posts.");
        return posts;
    }

    public List<NotificationSettingModel> getNotificationSettings() throws IOException {
        int userId = AppSettings.getInstance().getUserId();
        Call<List<NotificationSettingModel>> call = apiEndpoints.getNotificationSettings(userId);
        Response<List<NotificationSettingModel>> response = call.execute();

        if (!response.isSuccessful()) {
            Log.e("PostClient", "Error fetching posts: " + response.code() + " - " + response.message());
            return new ArrayList<>();
        }

        List<NotificationSettingModel> settings = response.body();
        if (settings == null) {
            Log.w("PostClient", "Received null response body for posts.");
            return new ArrayList<>();
        }

        Log.d("PostClient", "Fetched " + settings.size() + " posts.");
        return settings;
    }

    public Void setNotificationSettings(List<NotificationSettingModel> settings) throws IOException {
        Call<Void> call = apiEndpoints.setNotificationSettings(settings);
        Response<Void> response = call.execute();
        return null;
    }

    public boolean getNotificationFlag(int userId) throws IOException {
        //Call<Boolean> call = apiEndpoints.getNotificationFlag(userId);
        //Response<Boolean> response = call.execute();
        //return response.body();
        return true;
    }


    public boolean getMessageFlag(int userId) throws IOException {
        //Call<Boolean> call = apiEndpoints.getMessageFlag(userId);
        //Response<Boolean> response = call.execute();
        //return response.body();
        return true;
    }

    public boolean getDeviceNotificationFlag(int userId) throws IOException {
        //Call<Boolean> call = apiEndpoints.getDeviceNotificationFlag(userId);
        //Response<Boolean> response = call.execute();
        //return response.body();
        return true;
    }

}
