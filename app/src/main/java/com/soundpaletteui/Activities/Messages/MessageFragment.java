package com.soundpaletteui.Activities.Messages;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Activities.Posts.PostAdapter;
import com.soundpaletteui.Activities.Posts.PostFragment;
import com.soundpaletteui.Activities.Profile.ProfileViewFragment;
import com.soundpaletteui.Infrastructure.Adapters.MainContentAdapter;
import com.soundpaletteui.Infrastructure.ApiClients.ChatClient;
import com.soundpaletteui.Infrastructure.ApiClients.PostClient;
import com.soundpaletteui.Infrastructure.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.ChatroomModel;
import com.soundpaletteui.Infrastructure.Models.PostModel;
import com.soundpaletteui.Infrastructure.Models.UserModel;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import com.soundpaletteui.Infrastructure.Utilities.DarkModePreferences;
import com.soundpaletteui.R;

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
    private ImageButton createChatroomButton;
    private ArrayList<ChatroomModel> allChatrooms = new ArrayList<>();
    private final ChatClient messageClient = SPWebApiRepository.getInstance().getChatClient();

    public MessageFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_message, container, false);
        boolean isDarkMode = DarkModePreferences.isDarkModeEnabled(rootView.getContext());
        UISettings.applyBrightnessGradientBackground(rootView, 240f, isDarkMode);

        initComponents(rootView);
        messagesRecyclerView = rootView.findViewById(R.id.recyclerViewMessages);
        messagesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));

        createChatroomButton = rootView.findViewById(R.id.createChatroomButton);
        createChatroomButton.setOnClickListener(v -> {
            NewChatroomFragment newChatroomFragment = NewChatroomFragment.newInstance();
            FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
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
