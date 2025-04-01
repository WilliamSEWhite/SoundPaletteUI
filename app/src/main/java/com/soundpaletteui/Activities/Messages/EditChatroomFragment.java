package com.soundpaletteui.Activities.Messages;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Infrastructure.Models.UserProfileModelLite;
import com.soundpaletteui.Infrastructure.Models.UserModel;
import com.soundpaletteui.Activities.SearchAdapters.UserSearchAdapter;
import com.soundpaletteui.Infrastructure.ApiClients.ChatClient;
import com.soundpaletteui.Infrastructure.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditChatroomFragment extends Fragment {

    private static final String ARG_CHATROOM_ID = "chatRoomId";
    private static final String ARG_CHATROOM_NAME = "chatRoomName";

    private int chatRoomId;
    private String chatRoomName, newChatroomName;
    private EditText chatroomNameEdit, userSearchInput;
    private RecyclerView userSearchResults, selectedUsersView;
    private Button saveButton;

    private UserClient userClient;
    private ChatClient chatClient;
    private UserModel currentUser;

    private List<UserProfileModelLite> selectedUsers = new ArrayList<>();
    private List<UserProfileModelLite> searchResults = new ArrayList<>();

    private UserSearchAdapter userSearchAdapter;
    private com.soundpaletteui.Activities.SearchAdapters.UserSelectedAdapter selectedUsersAdapter;

    public EditChatroomFragment() {}

    public static EditChatroomFragment newInstance(int chatroomId, String chatroomName) {
        EditChatroomFragment fragment = new EditChatroomFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CHATROOM_ID, chatroomId);
        args.putString(ARG_CHATROOM_NAME, chatroomName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            chatRoomId = getArguments().getInt(ARG_CHATROOM_ID);
            chatRoomName = getArguments().getString(ARG_CHATROOM_NAME);
        }
        currentUser = AppSettings.getInstance().getUser();
        userClient = SPWebApiRepository.getInstance().getUserClient();
        chatClient = SPWebApiRepository.getInstance().getChatClient();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chatroom_setup, container, false);

        chatroomNameEdit = rootView.findViewById(R.id.chatroomNameEdit);
        userSearchInput = rootView.findViewById(R.id.userSearchInput);
        userSearchResults = rootView.findViewById(R.id.userSearchResults);
        selectedUsersView = rootView.findViewById(R.id.selectedUsersView);
        saveButton = rootView.findViewById(R.id.saveButton);

        chatroomNameEdit.setText(chatRoomName);

        userSearchAdapter = new UserSearchAdapter(searchResults, user -> {
            if (!selectedUsers.contains(user)) {
                selectedUsers.add(user);
                selectedUsersAdapter.notifyDataSetChanged();
            }
        });

        selectedUsersAdapter = new com.soundpaletteui.Activities.SearchAdapters.UserSelectedAdapter(selectedUsers, user -> {
            selectedUsers.remove(user);
            selectedUsersAdapter.notifyDataSetChanged();
        });

        userSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        userSearchResults.setAdapter(userSearchAdapter);

        selectedUsersView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        selectedUsersView.setAdapter(selectedUsersAdapter);

        populateDummyUsersAsync();
        loadChatroomMembersAsync();

        userSearchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        saveButton.setOnClickListener(v -> {
            newChatroomName = chatroomNameEdit.getText().toString().trim();
            if (!TextUtils.isEmpty(newChatroomName)) {
                new UpdateChatroomAsync().execute();
            }

            ChatroomFragment chatroomFragment = ChatroomFragment.newInstance(chatRoomId, chatRoomName);
            FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.mainScreenFrame, chatroomFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return rootView;
    }

    private void searchUsers(String query){
        Log.d("EditChatroomFragment", "Search for users containing "+query);
    }

    private void populateDummyUsersAsync() {
        new Thread(() -> {
            try {
                UserClient client = SPWebApiRepository.getInstance().getUserClient();
                List<UserProfileModelLite> results = new ArrayList<>();
                results.add(client.getUserProfileByUsername("user1"));
                results.add(client.getUserProfileByUsername("user2"));
                results.add(client.getUserProfileByUsername("user3"));
                results.add(client.getUserProfileByUsername("user4"));

                requireActivity().runOnUiThread(() -> {
                    searchResults.clear();
                    searchResults.addAll(results);
                    userSearchAdapter.notifyDataSetChanged();
                });

            } catch (IOException e) {
                Log.e("EditChatroomFragment", "Failed to load dummy users: " + e.getMessage());
            }
        }).start();
    }

    private void loadChatroomMembersAsync() {
        new Thread(() -> {
            try {
                List<String> chatroomMembers = chatClient.getChatroomMembers(chatRoomId);
                List<UserProfileModelLite> userProfitModelList = new ArrayList<>();

                for (String username : chatroomMembers) {
                    try {
                        UserProfileModelLite profile = userClient.getUserProfileByUsername(username);
                        if (profile != null) {
                            userProfitModelList.add(profile);
                        }
                    } catch (IOException e) {
                        Log.e("EditChatroomFragment", "Failed to fetch profile for " + username + ": " + e.getMessage());
                    }
                }

                requireActivity().runOnUiThread(() -> {
                    selectedUsers.clear();
                    selectedUsers.addAll(userProfitModelList);
                    selectedUsersAdapter.notifyDataSetChanged();
                });

            } catch (IOException e) {
                Log.e("EditChatroomFragment", "Failed to get member usernames: " + e.getMessage());
            }
        }).start();
    }


    private class UpdateChatroomAsync extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... d) {
            try {
                List<UserProfileModelLite> memberModels = new ArrayList<>();
                for (UserProfileModelLite user : selectedUsers) {
                    memberModels.add(new UserProfileModelLite(user.getUsername()));
                }
                chatClient.updateChatroom(chatRoomId, newChatroomName, memberModels);
            } catch (IOException e) {
                Log.e("EDIT CHATROOM", "Failed to update chatroom: " + e.getMessage());
            }
            return null;
        }
    }
}
