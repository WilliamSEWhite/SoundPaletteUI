package com.soundpaletteui.SPApiServices.ApiEndpoints;

import com.soundpaletteui.Infrastructure.Models.Notification;
import com.soundpaletteui.Infrastructure.Models.NotificationSetting;
import com.soundpaletteui.Infrastructure.Models.Post.PostModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NotificationApiEndpoints {
    @GET("api/post/get-notifications")
    Call<List<Notification>> getNotification(@Query("userId") int userId);
    @GET("api/post/get-notification-settings")
    Call<List<NotificationSetting>> getNotificationSettings(@Query("userId") int userId);
    @POST("api/post/set-notification-settings")
    Call<Void> setNotificationSettings(@Body List<NotificationSetting> settings);

}
