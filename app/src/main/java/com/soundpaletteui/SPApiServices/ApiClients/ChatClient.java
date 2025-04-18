package com.soundpaletteui.SPApiServices.ApiClients;

import com.soundpaletteui.SPApiServices.ApiEndpoints.ChatApiEndpoints;
import com.soundpaletteui.Infrastructure.Models.Chat.ChatMessageModel;
import com.soundpaletteui.Infrastructure.Models.Chat.ChatroomInfoModel;
import com.soundpaletteui.Infrastructure.Models.Chat.ChatroomModel;
import com.soundpaletteui.Infrastructure.Models.Chat.ChatroomModelLite;
import com.soundpaletteui.Infrastructure.Models.Chat.NewChatroomModel;
import com.soundpaletteui.Infrastructure.Models.Chat.NewMessageModel;
import com.soundpaletteui.Infrastructure.Models.Chat.ChatroomUpdateModel;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChatClient {
    private static ChatApiEndpoints apiEndpoints;
    public ChatClient(Retrofit retrofit) {
        apiEndpoints = retrofit.create(ChatApiEndpoints.class);
    }

    public List<ChatroomModel> getChatrooms() throws IOException {
        int userId = AppSettings.getInstance().getUserId();

        Call<List<ChatroomModel>> call = apiEndpoints.getChatrooms(userId);
        Response<List<ChatroomModel>> response = call.execute();

        return response.body();
    }

    public List<ChatMessageModel> getMessagesForChatroom(int chatroomId) throws IOException {
        Call<List<ChatMessageModel>> call = apiEndpoints.getMessagesForChatroom(chatroomId);
        Response<List<ChatMessageModel>> response = call.execute();

        return response.body();
    }
    public ChatroomModelLite getPrivateChatroom(String username) throws IOException {
        int userId = AppSettings.getInstance().getUserId();

        Call<ChatroomModelLite> call = apiEndpoints.getPrivateChatroom(userId, username);
        Response<ChatroomModelLite> response = call.execute();

        return response.body();
    }
    public ChatroomModelLite createChatroom(NewChatroomModel newChatroom) throws IOException {
        Call<ChatroomModelLite> call = apiEndpoints.createChatroom(newChatroom);
        Response<ChatroomModelLite> response = call.execute();

        return response.body();
    }
    public Void sendMessage(NewMessageModel newMessage) throws IOException {
        Call<Void> call = apiEndpoints.sendMessage(newMessage);
        Response<Void> response = call.execute();

        return response.body();
    }
    public Void removeUserFromChatroom(int chatroomId) throws IOException {
        int userId = AppSettings.getInstance().getUserId();

        Call<Void> call = apiEndpoints.removeUserFromChatroom(chatroomId, userId);
        Response<Void> response = call.execute();

        return response.body();
    }
    public void updateChatroom(ChatroomUpdateModel chatroomUpdate) throws IOException {
        Call<Void> call = apiEndpoints.updateChatroom(chatroomUpdate);
        Response<Void> response = call.execute();

        if (!response.isSuccessful()) {
            throw new IOException("Failed to update chatroom: " + response.code());
        }
    }
    public ChatroomInfoModel getChatroomInfo(int chatroomId) throws IOException {
        int userId = AppSettings.getInstance().getUserId();
        Call<ChatroomInfoModel> call = apiEndpoints.getChatroomInfo(userId, chatroomId);
        Response<ChatroomInfoModel> response = call.execute();
        return response.body();

    }
}
