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
import com.soundpaletteui.Views.EmojiBackgroundView;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_chatroom_setup, container, false);

        // 1) Apply the same brightness-gradient & emoji background as Search
        View rootLayout = rootView.findViewById(R.id.root_layout);
        boolean isDark = DarkModePreferences.isDarkModeEnabled(rootLayout.getContext());
        UISettings.applyBrightnessGradientBackground(rootLayout, 200f, isDark);

        EmojiBackgroundView emojiBg = rootView.findViewById(R.id.emojiBackground);
        emojiBg.setPatternType(EmojiBackgroundView.PATTERN_SPIRAL);
        emojiBg.setAlpha(0.65f);

        // 2) Your existing initialization
        chatroomNameEdit   = rootView.findViewById(R.id.chatroomNameEdit);
        userSearchInput    = rootView.findViewById(R.id.userSearchInput);
        userSearchResults  = rootView.findViewById(R.id.userSearchResults);
        selectedUsersView  = rootView.findViewById(R.id.selectedUsersView);
        saveButton         = rootView.findViewById(R.id.saveButton);

        currentUser = AppSettings.getInstance().getUser();
        userClient  = SPWebApiRepository.getInstance().getUserClient();
        chatClient  = SPWebApiRepository.getInstance().getChatClient();

        userSearchAdapter = new UserSearchAdapter(searchResults, username -> {
            if (!selectedUsers.contains(username)) {
                selectedUsers.add(username);
                selectedUsersAdapter.notifyDataSetChanged();
            }
        });
        selectedUsersAdapter = new UserSelectedAdapter(selectedUsers, username -> {
            selectedUsers.remove(username);
            selectedUsersAdapter.notifyDataSetChanged();
        });

        userSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        userSearchResults.setAdapter(userSearchAdapter);

        selectedUsersView.setLayoutManager(
                new LinearLayoutManager(getContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false)
        );
        selectedUsersView.setAdapter(selectedUsersAdapter);

        userSearchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                if (s.length() > 3) searchUsersAsync(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        saveButton.setOnClickListener(v -> {
            String roomName = chatroomNameEdit.getText().toString().trim();
            if (selectedUsers.isEmpty()) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Error")
                        .setMessage("Chatroom users cannot be empty.")
                        .setPositiveButton("OK", null)
                        .show();
            } else {
                new CreateChatroomTaskAsync().execute(roomName);
            }
        });

        return rootView;
    }

    private class CreateChatroomTaskAsync
            extends AsyncTask<String, Void, ChatroomModelLite> {
        @Override
        protected ChatroomModelLite doInBackground(String... params) {
            try {
                String name = params[0];
                List<String> users = new ArrayList<>(selectedUsers);
                users.add(currentUser.getUsername());
                return chatClient.createChatroom(
                        new NewChatroomModel(name, users)
                );
            } catch (IOException e) {
                return null;
            }
        }
        @Override
        protected void onPostExecute(ChatroomModelLite chatroom) {
            if (chatroom != null) {
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                FragmentTransaction tx = fm.beginTransaction();
                tx.replace(
                        R.id.mainScreenFrame,
                        ChatroomFragment.newInstance(chatroom.getChatroomId(), chatroom.getName())
                );
                tx.addToBackStack(null).commit();
            } else {
                new AlertDialog.Builder(getContext())
                        .setTitle("Error")
                        .setMessage("Could not create chatroom.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        }
    }

    private void searchUsersAsync(String searchTerm) {
        new Thread(() -> {
            try {
                List<String> results =
                        SPWebApiRepository.getInstance()
                                .getUserClient()
                                .searchUsersLite(searchTerm);

                requireActivity().runOnUiThread(() -> {
                    searchResults.clear();
                    searchResults.addAll(results);
                    userSearchAdapter.notifyDataSetChanged();
                });
            } catch (IOException e) {/* log if needed */}
        }).start();
    }
}
