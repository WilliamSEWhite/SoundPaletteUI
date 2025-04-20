package com.soundpaletteui.SPApiServices.ApiClients;

import android.util.Log;

import com.soundpaletteui.Infrastructure.Models.Notification;
import com.soundpaletteui.Infrastructure.Models.NotificationSetting;
import com.soundpaletteui.Infrastructure.Models.Post.PostModel;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.SPApiServices.ApiEndpoints.NotificationApiEndpoints;
import com.soundpaletteui.SPApiServices.ApiEndpoints.PostApiEndpoints;

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
    public List<Notification> getNotification() throws IOException {
        int userId = AppSettings.getInstance().getUserId();
        Call<List<Notification>> call = apiEndpoints.getNotification(userId);
        Response<List<Notification>> response = call.execute();

        if (!response.isSuccessful()) {
            Log.e("PostClient", "Error fetching posts: " + response.code() + " - " + response.message());
            return new ArrayList<>();
        }

        List<Notification> posts = response.body();
        if (posts == null) {
            Log.w("PostClient", "Received null response body for posts.");
            return new ArrayList<>();
        }

        Log.d("PostClient", "Fetched " + posts.size() + " posts.");
        return posts;
    }

    public List<NotificationSetting> getNotificationSettings() throws IOException {
        int userId = AppSettings.getInstance().getUserId();
        Call<List<NotificationSetting>> call = apiEndpoints.getNotificationSettings(userId);
        Response<List<NotificationSetting>> response = call.execute();

        if (!response.isSuccessful()) {
            Log.e("PostClient", "Error fetching posts: " + response.code() + " - " + response.message());
            return new ArrayList<>();
        }

        List<NotificationSetting> settings = response.body();
        if (settings == null) {
            Log.w("PostClient", "Received null response body for posts.");
            return new ArrayList<>();
        }

        Log.d("PostClient", "Fetched " + settings.size() + " posts.");
        return settings;
    }

    public Void setNotificationSettings(List<NotificationSetting> settings) throws IOException {
        Call<Void> call = apiEndpoints.setNotificationSettings(settings);
        Response<Void> response = call.execute();
        return null;
    }
}
