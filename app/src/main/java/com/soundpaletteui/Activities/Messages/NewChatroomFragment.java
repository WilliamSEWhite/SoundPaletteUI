package com.soundpaletteui.Activities.Messages;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.soundpaletteui.Infrastructure.Models.Chat.ChatroomModelLite;
import com.soundpaletteui.Infrastructure.Models.Chat.NewChatroomModel;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.Infrastructure.Utilities.DarkModePreferences;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import com.soundpaletteui.R;
import com.soundpaletteui.SPApiServices.ApiClients.ChatClient;
import com.soundpaletteui.SPApiServices.ApiClients.UserClient;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.EmojiBackgroundView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Displays the New Chatroom creation screen where users can name a chatroom, search for users, select members, and create a new chatroom.
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

    // Creates a new instance of NewChatroomFragment
    public static NewChatroomFragment newInstance() {
        return new NewChatroomFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_chatroom_setup, container, false);

        // Apply gradient and emoji background
        View rootLayout = rootView.findViewById(R.id.root_layout);
        boolean isDark = DarkModePreferences.isDarkModeEnabled(rootLayout.getContext());
        UISettings.applyBrightnessGradientBackground(rootLayout, 200f, isDark);

        EmojiBackgroundView emojiBg = rootView.findViewById(R.id.emojiBackground);
        emojiBg.setPatternType(EmojiBackgroundView.PATTERN_SPIRAL);
        emojiBg.setAlpha(0.65f);

        // Initialize UI components
        chatroomNameEdit   = rootView.findViewById(R.id.chatroomNameEdit);
        userSearchInput    = rootView.findViewById(R.id.userSearchInput);
        userSearchResults  = rootView.findViewById(R.id.userSearchResults);
        selectedUsersView  = rootView.findViewById(R.id.selectedUsersView);
        saveButton         = rootView.findViewById(R.id.saveButton);

        // Load current user and API clients
        currentUser = AppSettings.getInstance().getUser();
        userClient  = SPWebApiRepository.getInstance().getUserClient();
        chatClient  = SPWebApiRepository.getInstance().getChatClient();

        // Set up user search results adapter
        userSearchAdapter = new UserSearchAdapter(searchResults, username -> {
            if (!selectedUsers.contains(username)) {
                selectedUsers.add(username);
                selectedUsersAdapter.notifyDataSetChanged();
            }
        });

        // Set up selected users adapter
        selectedUsersAdapter = new UserSelectedAdapter(selectedUsers, username -> {
            selectedUsers.remove(username);
            selectedUsersAdapter.notifyDataSetChanged();
        });

        // Set up RecyclerViews
        userSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        userSearchResults.setAdapter(userSearchAdapter);

        selectedUsersView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        selectedUsersView.setAdapter(selectedUsersAdapter);

        // Add a TextWatcher to the search bar to search users as user types
        userSearchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) searchUsersAsync(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Save button click: Validate selection and create the chatroom
        saveButton.setOnClickListener(v -> {
            String roomName = chatroomNameEdit.getText().toString().trim();
            if (selectedUsers.isEmpty()) {
                // Show error if no users selected
                new AlertDialog.Builder(getContext())
                        .setTitle("Error")
                        .setMessage("Chatroom users cannot be empty.")
                        .setPositiveButton("OK", null)
                        .show();
            } else {
                // Start AsyncTask to create the chatroom
                new CreateChatroomTaskAsync().execute(roomName);
            }
        });

        return rootView;
    }

    // AsyncTask class to create a new chatroom in the background
    private class CreateChatroomTaskAsync extends AsyncTask<String, Void, ChatroomModelLite> {
        @Override
        protected ChatroomModelLite doInBackground(String... params) {
            try {
                String name = params[0];

                // Add selected users plus current user to the chatroom
                List<String> users = new ArrayList<>(selectedUsers);
                users.add(currentUser.getUsername());

                // Create chatroom through the API
                return chatClient.createChatroom(
                        new NewChatroomModel(name, users, AppSettings.getInstance().getUserId())
                );
            } catch (IOException e) {
                // Return null if there's an error
                return null;
            }
        }

        @Override
        protected void onPostExecute(ChatroomModelLite chatroom) {
            if (chatroom != null) {
                // If created successfully, navigate to the new ChatroomFragment
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                FragmentTransaction tx = fm.beginTransaction();
                tx.replace(
                        R.id.mainScreenFrame,
                        ChatroomFragment.newInstance(chatroom.getChatroomId(), chatroom.getName())
                );
                tx.addToBackStack(null).commit();
            } else {
                // Otherwise, show an error
                new AlertDialog.Builder(getContext())
                        .setTitle("Error")
                        .setMessage("Could not create chatroom.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        }
    }

    // Searches for users asynchronously based on the input term
    private void searchUsersAsync(String searchTerm) {
        new Thread(() -> {
            try {
                List<String> results = SPWebApiRepository.getInstance()
                        .getUserClient()
                        .searchUsersLite(searchTerm);

                // Update the UI with the search results
                requireActivity().runOnUiThread(() -> {
                    searchResults.clear();
                    searchResults.addAll(results);
                    userSearchAdapter.notifyDataSetChanged();
                });
            } catch (IOException e) {
                // Handle or log error if needed
            }
        }).start();
    }
}
