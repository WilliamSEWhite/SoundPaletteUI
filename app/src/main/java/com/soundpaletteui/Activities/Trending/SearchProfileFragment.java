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

import com.soundpaletteui.Activities.Trending.SearchProfileAdapter;
import com.soundpaletteui.Infrastructure.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.UserProfileModelLite;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchProfileFragment extends Fragment {
    private static final String ARG_SEARCH_TERM = "usernameSearch";
    private String usernameSearch;
    private ArrayList<UserProfileModelLite> allProfiles = new ArrayList<>();
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

        new SearchProfileFragment.GetProfilesTask().execute();
        return view;
    }

    // Gets Profiles from the client
    private class GetProfilesTask extends AsyncTask<Void, Void, List<UserProfileModelLite>> {
        @Override
        protected List<UserProfileModelLite> doInBackground(Void... voids) {
            List<UserProfileModelLite> profiles = null;
            profiles = userClient.getProfilesForUsername(usernameSearch);
            return profiles;
        }

        @Override
        protected void onPostExecute(List<UserProfileModelLite> profiles) {
            if (profiles == null) {
                Log.w("Search Profule Fragment", "Received null profile list, initializing empty list");
                profiles = new ArrayList<>();
            }
            Log.d("Search Profule Fragment", "Fetched dummy posts: " + profiles.size());
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
}
