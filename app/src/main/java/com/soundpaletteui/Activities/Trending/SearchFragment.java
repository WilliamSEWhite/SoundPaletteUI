package com.soundpaletteui.Activities.Trending;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.soundpaletteui.Activities.Posts.PostFragment;
import com.soundpaletteui.Infrastructure.Adapters.MainContentAdapter;
import com.soundpaletteui.Infrastructure.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.UserModel;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.R;
import com.soundpaletteui.UISettings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Provides search functionality and displays relevant posts.
 */
public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private MainContentAdapter mainContentAdapter;
    private List<UserModel> userList;
    private int userId;
    private UserModel user;
    private UserClient userClient;

    /**
     * Default constructor for SearchFragment.
     */
    public SearchFragment() {
    }

    /**
     * Returns a new instance of SearchFragment with the specified userId.
     */
    public static SearchFragment newInstance(int userId) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt("USER_ID", userId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Initializes data from arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", -1);
        }
    }

    /**
     * Inflates the layout, sets up UI, and loads initial post content.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        UISettings.applyBrightnessGradientBackground(rootView, 330f);
        initComponents(rootView);
        Random random = new Random();
        int randomNumber = random.nextInt(6) + 10;
        Log.d("SearchFragment", "Initial randomNumber: " + randomNumber);
        replaceFragment(randomNumber);
        EditText inputSearch = rootView.findViewById(R.id.edittext_search);
        ImageButton buttonSearch = rootView.findViewById(R.id.button_search);
        buttonSearch.setOnClickListener(v -> {
            String searchText = inputSearch.getText().toString().trim();
            Log.d("SearchFragment", "Fetching new feed with ID: " + searchText);
            if (isNumeric(searchText)) {
                int searchNumber = Integer.parseInt(searchText);
                if (searchNumber >= 1 && searchNumber <= 9) {
                    replaceFragment(searchNumber);
                }
            } else {
                if (searchText.toLowerCase().equals("art")) {
                    replaceFragment(7);
                } else if (searchText.toLowerCase().equals("music")) {
                    replaceFragment(8);
                } else {
                    Toast.makeText(requireContext(), "I heard you're trying to find " + searchText, Toast.LENGTH_SHORT).show();
                }
            }
        });
        return rootView;
    }

    /**
     * Checks if a string is numeric.
     */
    private boolean isNumeric(String str) {
        return str != null && !str.isEmpty() && str.matches("\\d+");
    }

    /**
     * Initializes the main content adapter and loads user data.
     */
    private void initComponents(View view) {
        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", 0);
        }
        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);
        userClient = SPWebApiRepository.getInstance().getUserClient();
        getUser();
    }

    /**
     * Retrieves user data in a background thread.
     */
    private void getUser() {
        new Thread(() -> {
            try {
                user = userClient.getUser(userId);
                requireActivity().runOnUiThread(this::populateView);
            } catch (IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Error fetching user", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    /**
     * Updates the adapter list once user data is retrieved.
     */
    private void populateView() {
        if (user != null) {
            userList.clear();
            userList.add(user);
            mainContentAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Replaces the child fragment with a PostFragment having a fixed hue.
     */
    private void replaceFragment(int id) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        PostFragment postFragment = PostFragment.newInstance(id, 330f);
        transaction.replace(R.id.postFragment, postFragment);
        transaction.commit();
    }
}
