package com.soundpaletteui.Activities.Messages;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Infrastructure.Utilities.DarkModePreferences;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import com.soundpaletteui.SPApiServices.ApiClients.ChatClient;
import com.soundpaletteui.SPApiServices.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.Chat.ChatroomModel;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.R;
import com.soundpaletteui.Views.EmojiBackgroundView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {
    private List<UserModel> userList;
    private UserModel user;
    private UserClient userClient;
    private int userId;
    private String username;
    private RecyclerView messagesRecyclerView;
    private View createChatroomButton;
    private ArrayList<ChatroomModel> allChatrooms = new ArrayList<>();
    private final ChatClient messageClient = SPWebApiRepository.getInstance().getChatClient();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_message, container, false);

        // Get and configure the emoji background
        EmojiBackgroundView emojiBackground = rootView.findViewById(R.id.emojiBackground);
        emojiBackground.setPatternType(EmojiBackgroundView.PATTERN_GRID);
        emojiBackground.setAlpha(0.2f);
        boolean isDarkMode = DarkModePreferences.isDarkModeEnabled(rootView.getContext());
        UISettings.applyBrightnessGradientBackground(rootView, 240f, isDarkMode);

        initComponents(rootView);

        messagesRecyclerView = rootView.findViewById(R.id.recyclerViewMessages);
        messagesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));

        createChatroomButton = rootView.findViewById(R.id.createChatroomButton);
        createChatroomButton.setOnClickListener(v -> {
            NewChatroomFragment newChatroomFragment = NewChatroomFragment.newInstance();
            FragmentActivity activity = (FragmentActivity) v.getContext();
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.mainScreenFrame, newChatroomFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        new GetChatroomsTask().execute();

        return rootView;
    }

    private void initComponents(View view) {
        user = AppSettings.getInstance().getUser();
        userClient = SPWebApiRepository.getInstance().getUserClient();
        if (user != null) {
            username = user.getUsername();
        } else {
            Log.e("MessageFragment", "User is null. Cannot initialize properly.");
        }
    }

    private class GetChatroomsTask extends AsyncTask<Void, Void, List<ChatroomModel>> {
        @Override
        protected List<ChatroomModel> doInBackground(Void... voids) {
            try {
                return messageClient.getChatrooms();
            } catch (IOException e) {
                Log.e("MESSAGES", "Error fetching chatrooms", e);
                return new ArrayList<>();
            }
        }
        @Override
        protected void onPostExecute(List<ChatroomModel> chatrooms) {
            if (chatrooms == null) {
                chatrooms = new ArrayList<>();
            }
            allChatrooms.clear();
            allChatrooms.addAll(chatrooms);
            setupRecyclerView();
        }
    }

    private void setupRecyclerView() {
        if (messagesRecyclerView.getAdapter() == null) {
            messagesRecyclerView.setAdapter(new ChatroomAdapter(allChatrooms));
        } else {
            messagesRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }
}
