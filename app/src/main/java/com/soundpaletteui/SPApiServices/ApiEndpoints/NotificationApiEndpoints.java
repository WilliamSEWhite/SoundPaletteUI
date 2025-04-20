package com.soundpaletteui.SPApiServices.ApiEndpoints;

import com.soundpaletteui.Infrastructure.Models.Notifications.NotificationModel;
import com.soundpaletteui.Infrastructure.Models.Notifications.NotificationSettingModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NotificationApiEndpoints {
    @GET("api/post/get-notifications")
    Call<List<NotificationModel>> getNotification(@Query("userId") int userId);
    @GET("api/post/get-notification-flag")
    Call<Boolean> getNotificationFlag(@Query("userId") int userId);
    @GET("api/post/get-device-notification-flag")
    Call<Boolean> getDeviceNotificationFlag(@Query("userId") int userId);
    @GET("api/post/get-message-flag")
    Call<Boolean> getMessageFlag(@Query("userId") int userId);
    @GET("api/post/get-notification-settings")
    Call<List<NotificationSettingModel>> getNotificationSettings(@Query("userId") int userId);
    @POST("api/post/set-notification-settings")
    Call<Void> setNotificationSettings(@Body List<NotificationSettingModel> settings);

}
