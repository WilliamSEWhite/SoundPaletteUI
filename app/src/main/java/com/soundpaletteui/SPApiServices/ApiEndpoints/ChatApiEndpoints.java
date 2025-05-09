package com.soundpaletteui.SPApiServices.ApiEndpoints;

import com.soundpaletteui.Infrastructure.Models.Chat.ChatMessageModel;
import com.soundpaletteui.Infrastructure.Models.Chat.ChatroomInfoModel;
import com.soundpaletteui.Infrastructure.Models.Chat.ChatroomModel;
import com.soundpaletteui.Infrastructure.Models.Chat.ChatroomModelLite;
import com.soundpaletteui.Infrastructure.Models.Chat.NewChatroomModel;
import com.soundpaletteui.Infrastructure.Models.Chat.NewMessageModel;
import com.soundpaletteui.Infrastructure.Models.Chat.ChatroomUpdateModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ChatApiEndpoints {
    @GET("api/chat/get-user-chatrooms")
    Call<List<ChatroomModel>> getChatrooms(@Query("userId") int userId);
    @GET("api/chat/get-messages-for-chatroom")
    Call<List<ChatMessageModel>> getMessagesForChatroom(@Query("chatroomId") int chatroomId);
    @GET("api/chat/get-private-chatroom")
    Call<ChatroomModelLite> getPrivateChatroom(@Query("userId") int userId, @Query("username") String username);
    @GET("api/chat/get-user-chatrooms")
    Call<List<String>> getChatroomMembers(@Query("chatroomId") int chatroomId);
    @POST("api/chat/create-chatroom")
    Call<ChatroomModelLite> createChatroom(@Body NewChatroomModel newChatRoom);
    @POST("api/chat/send-message")
    Call<Void> sendMessage(@Body NewMessageModel newMessage);
    @GET("api/chat/remove-user-from-chat")
    Call<Void> removeUserFromChatroom(@Query("chatroomId") int chatroomId, @Query("userId") int userId);
    @POST("api/chat/update-chatroom")
    Call<Void> updateChatroom(@Body ChatroomUpdateModel chatroomUpdate);
    @GET("api/chat/get-chatroom")
    Call<ChatroomInfoModel> getChatroomInfo(@Query("userId") int userId, @Query("chatroomId") int chatroomId);

}
