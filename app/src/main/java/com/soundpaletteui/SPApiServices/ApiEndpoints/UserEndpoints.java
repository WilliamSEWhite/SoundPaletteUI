package com.soundpaletteui.SPApiServices.ApiEndpoints;

import com.soundpaletteui.Infrastructure.Models.User.UserInfoModel;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.Infrastructure.Models.User.UserProfileModel;
import com.soundpaletteui.Infrastructure.Models.User.UserProfileModelLite;
import com.soundpaletteui.Infrastructure.Models.User.UserSearchModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserEndpoints {
    @GET("api/user/get-user/{id}")
    Call<UserModel> getUser(@Path("id") int id);
    @GET("api/user/get-user-by-name/{userName}")
    Call<UserModel> getUserByName(@Path("userName") String user);
    @POST("api/user/update-user-info/{id}")
    Call<UserModel> updateUserInfo(@Path("id") int id, @Body UserInfoModel userInfo);
    @POST("api/user/update-user-info")
    Call<UserInfoModel> updateUserInfo(@Body UserInfoModel userInfo);
    @GET("api/user/get-user-info/{id}")
    Call<UserInfoModel> getUserInfo(@Path("id") int id);
    @GET("api/user/get-user-profile/{id}")
    Call<UserProfileModel> getUserProfile(@Path("id") int id);
    @GET("api/user/get-user-profile-by-username/{username}")
    Call<UserProfileModelLite> getUserProfileByUsername(@Path("username") String username, @Query("userId") int userId);
    @POST("api/user/update-user-profile")
    Call<UserProfileModel> updateUserProfile(@Body UserProfileModel userProfile);
    @GET("api/user/follow-user")
    Call<Void> followUser(@Query("followerId") int followerId, @Query("followingUsername") String followingUsername);
    @GET("api/user/unfollow-user")
    Call<Void> unfollowUser(@Query("followerId") int followerId, @Query("followingUsername") String followingUsername);
    @GET("api/user/search-users-lite")
    Call<List<String>> searchUsersLite(@Query("userId") int userId, @Query("searchTerm") String searchTerm);
    @GET("api/user/search-users")
    Call<List<UserSearchModel>> searchUsers(@Query("userId") int userId, @Query("searchTerm") String searchTerm);


}
