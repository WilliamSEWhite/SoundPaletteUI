package com.soundpaletteui.Activities.Messages;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
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

import com.soundpaletteui.Infrastructure.Models.ChatroomInfoModel;
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

    private int chatRoomId;
    private String  newChatroomName;
    private EditText chatroomNameEdit, userSearchInput;
    private RecyclerView userSearchResults, selectedUsersView;
    private Button saveButton, leaveButton;

    private UserClient userClient;
    private ChatClient chatClient;
    private UserModel currentUser;

    private List<String> selectedUsers = new ArrayList<>();
    private List<String> searchResults = new ArrayList<>();

    private UserSearchAdapter userSearchAdapter;
    private com.soundpaletteui.Activities.SearchAdapters.UserSelectedAdapter selectedUsersAdapter;

    private ChatroomInfoModel chatroomInfo;

    public EditChatroomFragment() {}

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
        if (getArguments() != null) {
            chatRoomId = getArguments().getInt(ARG_CHATROOM_ID);
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
        leaveButton = rootView.findViewById(R.id.saveButton);


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


        userSearchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count)             {
                if(s.length()>3){
                    searchUsersAsync(s.toString());
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        saveButton.setOnClickListener(v -> {
            newChatroomName = chatroomNameEdit.getText().toString().trim();
            new UpdateChatroomAsync().execute();

            ChatroomFragment chatroomFragment = ChatroomFragment.newInstance(chatRoomId, chatroomInfo.getChatroomName());
            FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.mainScreenFrame, chatroomFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        leaveButton.setOnClickListener(v -> {
            newChatroomName = chatroomNameEdit.getText().toString().trim();
            new UpdateChatroomAsync().execute();
//            MessageFragment messageFragment = MessageFragment.newInstance(chatRoomId, chatroomInfo.getChatroomName());
//            FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
//            FragmentTransaction transaction = fragmentManager.beginTransaction();
//            transaction.replace(R.id.mainScreenFrame, MessageFragment);
//            transaction.addToBackStack(null);
//            transaction.commit();

        });

        new GetChatroomInfoAsync().execute();

        return rootView;
    }

    private void populateView(){
        selectedUsers.clear();
        selectedUsers.addAll(chatroomInfo.getChatroomMembers());
        selectedUsersAdapter.notifyDataSetChanged();

        chatroomNameEdit.setText(chatroomInfo.getChatroomName());

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


    private class UpdateChatroomAsync extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... d) {
            try {
                List<UserProfileModelLite> memberModels = new ArrayList<>();
//                for (UserProfileModelLite user : selectedUsers) {
//                    memberModels.add(new UserProfileModelLite(user.getUsername()));
//                }
                chatClient.updateChatroom(chatRoomId, newChatroomName, memberModels);
            } catch (IOException e) {
                Log.e("EDIT CHATROOM", "Failed to update chatroom: " + e.getMessage());
            }
            return null;
        }
    }
    private class GetChatroomInfoAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ChatClient client = SPWebApiRepository.getChatClient();
                chatroomInfo = client.getChatroomInfo(chatRoomId);
            } catch (IOException e) {
                Log.e("ChatroomFragment", "Error leaving chatroom", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            populateView();
        }
    }
    private class LeaveChatroomTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                ChatClient client = SPWebApiRepository.getChatClient();

                client.removeUserFromChatroom(chatRoomId);
                return true;
            } catch (IOException e) {
                Log.e("ChatroomFragment", "Error leaving chatroom", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
            }
        }
    }
}
