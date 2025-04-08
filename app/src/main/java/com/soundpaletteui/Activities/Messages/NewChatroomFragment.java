package com.soundpaletteui.Activities.Messages;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Activities.SearchAdapters.UserSearchAdapter;
import com.soundpaletteui.Activities.SearchAdapters.UserSelectedAdapter;
import com.soundpaletteui.SPApiServices.ApiClients.ChatClient;
import com.soundpaletteui.SPApiServices.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.Chat.ChatroomModelLite;
import com.soundpaletteui.Infrastructure.Models.Chat.NewChatroomModel;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewChatroomFragment extends Fragment {

    private EditText chatroomNameEdit, userSearchInput;
    private RecyclerView userSearchResults, selectedUsersView;
    private Button saveButton;

    private UserClient userClient;
    private ChatClient chatClient;
    private UserModel currentUser;

    private List<String> selectedUsers = new ArrayList<>();
    private List<String> searchResults = new ArrayList<>();

    private UserSearchAdapter userSearchAdapter;
    private UserSelectedAdapter selectedUsersAdapter;

    public NewChatroomFragment() {}

    public static NewChatroomFragment newInstance() {
        return new NewChatroomFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = AppSettings.getInstance().getUser();
        userClient = SPWebApiRepository.getInstance().getUserClient();
        chatClient = SPWebApiRepository.getInstance().getChatClient();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chatroom_setup, container, false);

        // Initialize UI components
        chatroomNameEdit = rootView.findViewById(R.id.chatroomNameEdit);
        userSearchInput = rootView.findViewById(R.id.userSearchInput);
        userSearchResults = rootView.findViewById(R.id.userSearchResults);
        selectedUsersView = rootView.findViewById(R.id.selectedUsersView);
        saveButton = rootView.findViewById(R.id.saveButton);

        // Set up adapters and RecyclerViews
        userSearchAdapter = new UserSearchAdapter(searchResults, user -> {
            if (!selectedUsers.contains(user)) {
                selectedUsers.add(user);
                selectedUsersAdapter.notifyDataSetChanged();
            }
        });

        // Action to remove the selected user from list
        selectedUsersAdapter = new UserSelectedAdapter(selectedUsers, user -> {
            selectedUsers.remove(user);
            selectedUsersAdapter.notifyDataSetChanged();
        });

        // Set up the list of users to select from
        userSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        userSearchResults.setAdapter(userSearchAdapter);

        // Set up the list of users selected
        selectedUsersView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        selectedUsersView.setAdapter(selectedUsersAdapter);

        // Dummy data for user search results
        userSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            // Prompt for API return at 3 letters, then again at 5+
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(s.length()>3){
                    searchUsersAsync(s.toString());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Save button listener
        saveButton.setOnClickListener(v -> {
            String chatRoomName = chatroomNameEdit.getText().toString().trim();

            // If the selected Users is empty
            if (selectedUsers.isEmpty()) {
                Log.d("NewChatroomFragment", "No users selected.");
                new AlertDialog.Builder(getContext())
                        .setTitle("Error")
                        .setMessage("Chatroom users cannot be empty.")
                        .setPositiveButton("OK", null)
                        .show();
            } else {
                // Create a new Chatroom with ChatroomName and List<String> usernames
                new CreateChatroomTaskAsync().execute();
            }
        });

        return rootView;
    }

    // Sends the actions to create a new Chatroom in the API Server
    private class CreateChatroomTaskAsync extends AsyncTask<Void, Void, ChatroomModelLite> {
        protected ChatroomModelLite doInBackground(Void... d) {
            try {
                String chatRoomName = chatroomNameEdit.getText().toString().trim();
                List<String> usernames = new ArrayList<>();
                usernames.addAll(selectedUsers);
                usernames.add(AppSettings.getInstance().getUsername());

                Log.d("NewChatroomFragment", "Creating a new Chatroom titled: " + chatRoomName);
                NewChatroomModel newChatroom = new NewChatroomModel(chatRoomName, usernames);
                return chatClient.createChatroom(newChatroom);

            } catch (IOException e) {
                Log.e("NewChatroomFragment", "Failed to create chatroom: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ChatroomModelLite createdChatroom) {
            if (createdChatroom != null) {
                openChatroom(createdChatroom);
            } else {
                new AlertDialog.Builder(getContext())
                        .setTitle("Error")
                        .setMessage("Chatroom could not be created. Please try again.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        }
    }


    private void openChatroom(ChatroomModelLite chatroom){
        ChatroomFragment chatroomFragment = ChatroomFragment.newInstance(chatroom.getChatroomId(), chatroom.getName());
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.mainScreenFrame, chatroomFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void searchUsersAsync(String searchTerm) {
        new Thread(() -> {
            try {
                UserClient client = SPWebApiRepository.getInstance().getUserClient();
                List<String> results = client.searchUsers(searchTerm);

                requireActivity().runOnUiThread(() -> {
                    searchResults.clear();
                    searchResults.addAll(results);
                    userSearchAdapter.notifyDataSetChanged();
                });

            } catch (IOException e) {
                Log.e("NewChatroomFragment", "Failed to load dummy users: " + e.getMessage());
            }
        }).start();
    }
}
