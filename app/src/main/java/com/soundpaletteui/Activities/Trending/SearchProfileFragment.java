package com.soundpaletteui.Activities.Trending;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Infrastructure.Models.User.UserSearchModel;
import com.soundpaletteui.SPApiServices.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.User.UserProfileModelLite;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.MediaPlayerManager;
import com.soundpaletteui.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Displays a list of user profiles based on a search term. Handles fetching profiles and showing them in a RecyclerView.
public class SearchProfileFragment extends Fragment {
    private static final String ARG_SEARCH_TERM = "usernameSearch";
    private String usernameSearch;
    private ArrayList<UserSearchModel> allProfiles = new ArrayList<>();
    private RecyclerView recyclerView;
    private final UserClient userClient = SPWebApiRepository.getInstance().getUserClient();
    public static SearchProfileFragment newInstance(String searchTerm) {
        SearchProfileFragment fragment = new SearchProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SEARCH_TERM, searchTerm);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.usernameSearch = getArguments().getString(ARG_SEARCH_TERM);
        }
    }

    // Sets the View to fragment_search_profile.xml (only contains the RecyclerView)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_profile, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));

        // Start loading profiles based on the search term
        new SearchProfileFragment.GetProfilesTask().execute();
        return view;
    }

    // Async task for getting user profiles from the server
    private class GetProfilesTask extends AsyncTask<Void, Void, List<UserSearchModel>> {
        @Override
        // Search for users with the provided search term
        protected List<UserSearchModel> doInBackground(Void... voids) {
            List<UserSearchModel> profiles = null;
            try {
                profiles = userClient.searchUsers(usernameSearch);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return profiles;
        }

        @Override
        protected void onPostExecute(List<UserSearchModel> profiles) {
            // If something went wrong, make sure the list isn't null
            if (profiles == null) {
                Log.w("Search Profule Fragment", "Received null profile list, initializing empty list");
                profiles = new ArrayList<>();
            }

            // Update the list of all profiles and refresh the RecyclerView
            allProfiles.clear();
            allProfiles.addAll(profiles);
            setupRecyclerView();
        }
    }

    // Sets the RecyclerView by sending through a List of all UserProfileModelLite
    private void setupRecyclerView() {
        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(new SearchProfileAdapter(allProfiles));
        } else {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    // Pauses the media player when user leaves the fragment
    @Override
    public void onPause() {
        super.onPause();
        MediaPlayerManager.getInstance().release();
    }
}
