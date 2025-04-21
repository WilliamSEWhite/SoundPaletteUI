package com.soundpaletteui.Activities.Messages;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import com.soundpaletteui.Infrastructure.Utilities.DarkModePreferences;
import com.soundpaletteui.R;
import com.soundpaletteui.SPApiServices.ApiClients.ChatClient;
import com.soundpaletteui.SPApiServices.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.Chat.ChatMessageModel;
import com.soundpaletteui.Infrastructure.Models.Chat.NewMessageModel;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;
import com.soundpaletteui.Views.EmojiBackgroundView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatroomFragment extends Fragment {

    private static final String ARG_CHATROOM_ID = "chatRoomId";
    private static final String ARG_CHATROOM_NAME = "chatRoomName";
    private int chatRoomId;
    private String chatRoomName;
    private RecyclerView recyclerView;
    private TextView chatroomNameDisplay;
    private EditText editTextMessage;
    private Button btnSend, editChatroomButton, leaveChatroomButton;
    private UserModel user;
    private UserClient userClient;
    private String username;
    private ChatMessageAdapter adapter;
    private List<ChatMessageModel> messageList = new ArrayList<>();
    private final ChatClient messageClient = SPWebApiRepository.getInstance().getChatClient();

    public ChatroomFragment() {}

    public static ChatroomFragment newInstance(int chatroomId, String chatroomName) {
        ChatroomFragment fragment = new ChatroomFragment();
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout wrapped in a FrameLayout with EmojiBackgroundView
        View rootView = inflater.inflate(R.layout.fragment_chatroom, container, false);

        // Get and set emoji background (using a radial pattern)
        EmojiBackgroundView emojiBackground = rootView.findViewById(R.id.emojiBackground);
        emojiBackground.setPatternType(EmojiBackgroundView.PATTERN_GRID);
        emojiBackground.setAlpha(0.25f);

        boolean isDarkMode = DarkModePreferences.isDarkModeEnabled(rootView.getContext());
        UISettings.applyBrightnessGradientBackground(rootView, 190f, isDarkMode);

        initComponents(rootView);

        recyclerView = rootView.findViewById(R.id.recyclerViewMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        chatroomNameDisplay = rootView.findViewById(R.id.chatroomName);
        chatroomNameDisplay.setText(chatRoomName);

        adapter = new ChatMessageAdapter(messageList);
        recyclerView.setAdapter(adapter);

        editTextMessage = rootView.findViewById(R.id.editTextMessage);
        btnSend = rootView.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(v -> {
            String message = editTextMessage.getText().toString().trim();
            if (!TextUtils.isEmpty(message)) {
                new SendMessageTask().execute(message);
                editTextMessage.setText("");
            }
        });

        editChatroomButton = rootView.findViewById(R.id.editChatroomButton);
        editChatroomButton.setOnClickListener(v -> {
            EditChatroomFragment editChatroomFragment = EditChatroomFragment.newInstance(chatRoomId);
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            Navigation.replaceFragment(fragmentManager, editChatroomFragment, "EDIT_CHATROOM_FRAGMENT", R.id.mainScreenFrame);
        });

        new LoadMessagesTask().execute();

        return rootView;
    }

    private void initComponents(View view) {
        user = AppSettings.getInstance().getUser();
        userClient = SPWebApiRepository.getInstance().getUserClient();
        if (user != null) {
            username = user.getUsername();
        } else {
            Log.e("ChatroomFragment", "User is null. Cannot initialize properly.");
        }
    }

    private class LoadMessagesTask extends AsyncTask<Void, Void, List<ChatMessageModel>> {
        @Override
        protected List<ChatMessageModel> doInBackground(Void... voids) {
            try {
                return messageClient.getMessagesForChatroom(chatRoomId);
            } catch (IOException e) {
                Log.e("ChatroomFragment", "Error loading messages", e);
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(List<ChatMessageModel> messages) {
            messageList.clear();
            messageList.addAll(messages);
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(messageList.size() - 1);
        }
    }

    private class SendMessageTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                NewMessageModel newMessage = new NewMessageModel(AppSettings.getInstance().getUserId(), params[0], chatRoomId);
                messageClient.sendMessage(newMessage);
                return true;
            } catch (IOException e) {
                Log.e("ChatroomFragment", "Error sending message", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                new LoadMessagesTask().execute();
            }
        }
    }
}
