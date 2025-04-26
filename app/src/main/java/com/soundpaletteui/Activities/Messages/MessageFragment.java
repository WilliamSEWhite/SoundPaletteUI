package com.soundpaletteui.Activities.Messages;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Infrastructure.Utilities.DarkModePreferences;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import com.soundpaletteui.SPApiServices.ApiClients.ChatClient;
import com.soundpaletteui.SPApiServices.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.Chat.ChatroomModel;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.R;
import com.soundpaletteui.Infrastructure.Utilities.EmojiBackgroundView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Displays the list of user's chatrooms and lets them create a new one
public class MessageFragment extends Fragment {
    private UserModel user;
    private UserClient userClient;
    private String username;
    private RecyclerView messagesRecyclerView;
    private View createChatroomButton;
    private TextView noChatroomsDisplay;
    private ArrayList<ChatroomModel> allChatrooms = new ArrayList<>();
    private final ChatClient messageClient = SPWebApiRepository.getInstance().getChatClient();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_message, container, false);
        initComponents(rootView);
        return rootView;
    }

    // Initialize fragment components
    private void initComponents(View view) {
        noChatroomsDisplay = view.findViewById(R.id.noChatroomsDisplay);

        // Get and configure the emoji background
        EmojiBackgroundView emojiBackground = view.findViewById(R.id.emojiBackground);
        emojiBackground.setPatternType(EmojiBackgroundView.PATTERN_GRID);
        emojiBackground.setAlpha(0.2f);
        boolean isDarkMode = DarkModePreferences.isDarkModeEnabled(view.getContext());
        UISettings.applyBrightnessGradientBackground(view, 200f, isDarkMode);

        // Set up emoji background and brightness effect
        user = AppSettings.getInstance().getUser();
        userClient = SPWebApiRepository.getInstance().getUserClient();
        if (user != null) {
            username = user.getUsername();
        } else {
            Log.e("MessageFragment", "User is null. Cannot initialize properly.");
        }

        // Set up the RecyclerView
        messagesRecyclerView = view.findViewById(R.id.recyclerViewMessages);
        messagesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));

        // Set up "Create Chatroom" button to open new chatroom when select
        createChatroomButton = view.findViewById(R.id.createChatroomButton);
        createChatroomButton.setOnClickListener(v -> {
            NewChatroomFragment newChatroomFragment = NewChatroomFragment.newInstance();
            FragmentActivity activity = (FragmentActivity) v.getContext();
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            Navigation.replaceFragment(requireActivity().getSupportFragmentManager(),
                    transaction,
                    newChatroomFragment,
                    "NEW_CHATROOM_FRAGMENT",
                    R.id.mainScreenFrame);
        });
        new GetChatroomsTask().execute();
    }

    // Get the chatroom list
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

    // Configures the RecyclerView based on whether there are chatrooms
    private void setupRecyclerView() {
        // if no chatrooms, then display message
        if (allChatrooms.isEmpty()) {
            messagesRecyclerView.setVisibility(View.GONE);
            noChatroomsDisplay.setVisibility(View.VISIBLE);
            noChatroomsDisplay.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start();
        // else display available chatrooms
        } else {
            messagesRecyclerView.setVisibility(View.VISIBLE);
            noChatroomsDisplay.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction(() -> noChatroomsDisplay.setVisibility(View.GONE))
                    .start();

            // Set adapter if not already set
            if (messagesRecyclerView.getAdapter() == null) {
                messagesRecyclerView.setAdapter(new ChatroomAdapter(allChatrooms));
            } else {
                messagesRecyclerView.getAdapter().notifyDataSetChanged();
            }
        }
    }
}
