package com.soundpaletteui.Infrastructure.ApiClients;

import com.soundpaletteui.Infrastructure.ApiEndpoints.ChatApiEndpoints;
import com.soundpaletteui.Infrastructure.Models.ChatMessageModel;
import com.soundpaletteui.Infrastructure.Models.ChatroomModel;
import com.soundpaletteui.Infrastructure.Models.ChatroomModelLite;
import com.soundpaletteui.Infrastructure.Models.NewChatroomModel;
import com.soundpaletteui.Infrastructure.Models.NewMessageModel;
import com.soundpaletteui.Infrastructure.Models.UpdateChatroomModel;
import com.soundpaletteui.Infrastructure.Models.UserModel;
import com.soundpaletteui.Infrastructure.Models.UserProfileModelLite;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;

import java.io.IOException;
import java.util.ArrayList;
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
    public List<String> getChatroomMembers(int chatroomId) throws IOException {
        Call<List<String>> call = apiEndpoints.getChatroomMembers(chatroomId);
        Response<List<String>> response = call.execute();
        if (!response.isSuccessful()) {
            throw new IOException("Failed to fetch chatroom members: " + response.code());
        }
        return response.body();
    }
    public void updateChatroom(int chatroomId, String newName, List<UserProfileModelLite> members) throws IOException {
        List<String> usernames = new ArrayList<>();
        for (UserProfileModelLite user : members) {
            usernames.add(user.getUsername());
        }
        UpdateChatroomModel updatedChatroomModel = new UpdateChatroomModel(chatroomId, newName, usernames);
        Call<Void> call = apiEndpoints.updateChatroom(updatedChatroomModel);
        Response<Void> response = call.execute();

        if (!response.isSuccessful()) {
            throw new IOException("Failed to update chatroom: " + response.code());
        }
    }

}
