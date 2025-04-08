package com.soundpaletteui.Activities.Trending;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Infrastructure.Utilities.MediaPlayerManager;
import com.soundpaletteui.Activities.Posts.PostFragment;
import com.soundpaletteui.Infrastructure.Adapters.MainContentAdapter;
import com.soundpaletteui.SPApiServices.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import com.soundpaletteui.Infrastructure.Utilities.DarkModePreferences;
import com.soundpaletteui.R;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private MainContentAdapter mainContentAdapter;
    private List<UserModel> userList;
    private UserModel user;
    private UserClient userClient;

    private RadioGroup searchOptionsGroup;
    private EditText inputSearch;

    public SearchFragment() {
    }

    public static SearchFragment newInstance(int userId) {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        // Apply dark mode gradient background
        boolean isDarkMode = DarkModePreferences.isDarkModeEnabled(rootView.getContext());
        UISettings.applyBrightnessGradientBackground(rootView, 330f, isDarkMode);

        initComponents(rootView);

        inputSearch = rootView.findViewById(R.id.inputSearch);
        ImageButton buttonSearch = rootView.findViewById(R.id.buttonSearch);
        searchOptionsGroup = rootView.findViewById(R.id.searchOptionsGroup);

        replacePostFragment("trending", null);

        buttonSearch.setOnClickListener(v -> {
            String searchText = inputSearch.getText().toString().trim();
            getSelectedSearchOption(searchText);
        });

        return rootView;
    }

    private void initComponents(View view) {
        user = AppSettings.getInstance().getUser();
        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);
    }

    private void replacePostFragment(String algoType, String searchTerm) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        PostFragment postFragment = PostFragment.newInstance(algoType, searchTerm);
        transaction.replace(R.id.postFragment, postFragment);
        transaction.commit();
    }

    private void replacePostFragmentWithProfiles(String searchTerm) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        SearchProfileFragment searchProfileFragment = SearchProfileFragment.newInstance(searchTerm);
        transaction.replace(R.id.postFragment, searchProfileFragment);
        transaction.commit();
    }

    private void getSelectedSearchOption(String searchText) {
        int selectedId = searchOptionsGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.searchUsersRadio) {
            Log.d("SearchFragment", "Search Tags for: " + searchText);
            replacePostFragment("tags", searchText);
        } else if (selectedId == R.id.searchPostsRadio) {
            Log.d("SearchFragment", "Search Tags for: " + searchText);
            replacePostFragment("tags", searchText);
        } else if (selectedId == R.id.searchCaptionsRadio) {
            Log.d("SearchFragment", "Search Captions for: " + searchText);
            replacePostFragment("captions", searchText);
        } else {
        }
    }

    // Pauses the media player when user leaves the fragment
    @Override
    public void onPause() {
        super.onPause();
        MediaPlayerManager.getInstance().release();
    }
}
