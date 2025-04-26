package com.soundpaletteui.Activities.Messages;

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

import com.soundpaletteui.Infrastructure.Models.Chat.ChatroomInfoModel;
import com.soundpaletteui.Infrastructure.Models.Chat.ChatroomUpdateModel;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.Activities.SearchAdapters.UserSearchAdapter;
import com.soundpaletteui.Activities.SearchAdapters.UserSelectedAdapter;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import com.soundpaletteui.Infrastructure.Utilities.DarkModePreferences;
import com.soundpaletteui.R;
import com.soundpaletteui.SPApiServices.ApiClients.ChatClient;
import com.soundpaletteui.SPApiServices.ApiClients.UserClient;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.EmojiBackgroundView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Displays the Edit Chatroom screen where user can update name, members, or leave chatroom
public class EditChatroomFragment extends Fragment {

    private static final String ARG_CHATROOM_ID = "chatRoomId";
    private int chatRoomId;
    private UserClient userClient;
    private ChatClient chatClient;
    private UserModel currentUser;
    private ChatroomInfoModel chatroomInfo;

    private List<String> selectedUsers = new ArrayList<>();
    private List<String> searchResults = new ArrayList<>();
    private List<String> membersToAdd = new ArrayList<>();
    private List<String> membersToRemove = new ArrayList<>();

    private UserSearchAdapter userSearchAdapter;
    private UserSelectedAdapter selectedUsersAdapter;

    private String newChatroomName;
    private EditText chatroomNameEdit, userSearchInput;
    private RecyclerView userSearchResults, selectedUsersView;
    private Button saveButton, leaveButton;

    public EditChatroomFragment() {}

    // Creates a new instance with the chatroom ID
    public static EditChatroomFragment newInstance(int chatroomId) {
        EditChatroomFragment fragment = new EditChatroomFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CHATROOM_ID, chatroomId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load arguments and initialize clients
        if (getArguments() != null) {
            chatRoomId = getArguments().getInt(ARG_CHATROOM_ID);
        }
        currentUser = AppSettings.getInstance().getUser();
        userClient   = SPWebApiRepository.getInstance().getUserClient();
        chatClient   = SPWebApiRepository.getInstance().getChatClient();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_chatroom_setup, container, false);

        // Apply emoji + gradient just like Search screen
        View rootLayout = rootView.findViewById(R.id.root_layout);
        boolean isDark = DarkModePreferences.isDarkModeEnabled(rootLayout.getContext());
        UISettings.applyBrightnessGradientBackground(rootLayout, 200f, isDark);

        EmojiBackgroundView emojiBg = rootView.findViewById(R.id.emojiBackground);
        emojiBg.setPatternType(EmojiBackgroundView.PATTERN_SPIRAL);
        emojiBg.setAlpha(0.65f);

        // Bind views
        chatroomNameEdit   = rootView.findViewById(R.id.chatroomNameEdit);
        userSearchInput    = rootView.findViewById(R.id.userSearchInput);
        userSearchResults  = rootView.findViewById(R.id.userSearchResults);
        selectedUsersView  = rootView.findViewById(R.id.selectedUsersView);
        saveButton         = rootView.findViewById(R.id.saveButton);
        leaveButton        = rootView.findViewById(R.id.leaveChatroomButton);

        // Adapters
        userSearchAdapter = new UserSearchAdapter(searchResults, username -> {
            List<String> currentMembers = chatroomInfo.getChatroomMembers();
            if (!currentMembers.contains(username)) membersToAdd.add(username);
            if (membersToRemove.contains(username)) membersToRemove.remove(username);

            if (!selectedUsers.contains(username)) {
                selectedUsers.add(username);
                selectedUsersAdapter.notifyDataSetChanged();
            }
        });

        selectedUsersAdapter = new UserSelectedAdapter(selectedUsers, username -> {
            List<String> currentMembers = chatroomInfo.getChatroomMembers();
            if (currentMembers.contains(username)) membersToRemove.add(username);
            if (membersToAdd.contains(username)) membersToAdd.remove(username);

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

        // Search TextWatcher
        userSearchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                if (s.length() > 3) searchUsersAsync(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Save & Leave buttons
        saveButton.setOnClickListener(v -> {
            newChatroomName = chatroomNameEdit.getText().toString().trim();
            new UpdateChatroomAsync().execute();
        });
        leaveButton.setVisibility(View.VISIBLE);
        leaveButton.setOnClickListener(v -> new LeaveChatroomAsync().execute());

        // Load existing chatroom info
        new GetChatroomInfoAsync().execute();

        return rootView;
    }

    // Populates views after loading chatroom info
    private void populateView() {
        selectedUsers.clear();
        selectedUsers.addAll(chatroomInfo.getChatroomMembers());
        selectedUsersAdapter.notifyDataSetChanged();
        chatroomNameEdit.setText(chatroomInfo.getChatroomName());
    }

    // Searches users asynchronously based on search term
    private void searchUsersAsync(String term) {
        new Thread(() -> {
            try {
                List<String> results =
                        userClient.searchUsersLite(term);
                requireActivity().runOnUiThread(() -> {
                    searchResults.clear();
                    searchResults.addAll(results);
                    userSearchAdapter.notifyDataSetChanged();
                });
            } catch (IOException e) {
                Log.e("EditChat", "Search failed", e);
            }
        }).start();
    }

    // AsyncTask to get chatroom info
    private class GetChatroomInfoAsync extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... v) {
            try {
                chatroomInfo = chatClient.getChatroomInfo(chatRoomId);
            } catch (IOException e) {
                Log.e("EditChat", "Fetch info failed", e);
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void v) {
            populateView();
        }
    }

    // AsyncTask to update chatroom details
    private class UpdateChatroomAsync extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... v) {
            try {
                if (!Objects.equals(newChatroomName, chatroomInfo.getChatroomName())
                        || !membersToAdd.isEmpty()
                        || !membersToRemove.isEmpty()) {

                    chatClient.updateChatroom(
                            new ChatroomUpdateModel(
                                    chatroomInfo.getChatroomId(),
                                    newChatroomName,
                                    membersToAdd,
                                    membersToRemove
                            )
                    );
                }
            } catch (IOException e) {
                Log.e("EditChat", "Update failed", e);
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void v) {
            Navigation.replaceFragment(
                    requireActivity().getSupportFragmentManager(),
                    ChatroomFragment.newInstance(chatRoomId,
                            chatroomInfo.getChatroomName()),
                    "CHATROOM_FRAGMENT",
                    R.id.mainScreenFrame
            );
        }
    }

    // AsyncTask to leave the chatroom
    private class LeaveChatroomAsync extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... v) {
            try {
                chatClient.removeUserFromChatroom(chatRoomId);
                return true;
            } catch (IOException e) {
                Log.e("EditChat", "Leave failed", e);
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean ok) {
            if (ok) {
                Navigation.replaceFragment(
                        requireActivity().getSupportFragmentManager(),
                        new MessageFragment(),
                        "MESSAGE_FRAGMENT",
                        R.id.mainScreenFrame
                );
            }
        }
    }
}
