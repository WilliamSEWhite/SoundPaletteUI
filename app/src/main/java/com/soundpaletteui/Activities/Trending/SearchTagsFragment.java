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

import com.soundpaletteui.Activities.SearchAdapters.TagSearchAdapter;
import com.soundpaletteui.Infrastructure.Models.TagModel;
import com.soundpaletteui.Infrastructure.Models.User.UserSearchModel;
import com.soundpaletteui.SPApiServices.ApiClients.TagClient;
import com.soundpaletteui.SPApiServices.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.User.UserProfileModelLite;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.MediaPlayerManager;
import com.soundpaletteui.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Displays a list of tags based on a search term. Handles both browsing all tags and searching specific ones.
public class SearchTagsFragment extends Fragment {
    private static final String ARG_SEARCH_TERM = "tagsearchterm";
    private String searchTerm;
    private List<TagModel> tags = new ArrayList<>();
    private RecyclerView recyclerView;

    public static SearchTagsFragment newInstance(String searchTerm) {
        SearchTagsFragment fragment = new SearchTagsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SEARCH_TERM, searchTerm);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the search term that was sent to this screen
        if (getArguments() != null) {
            searchTerm = getArguments().getString(ARG_SEARCH_TERM);
        }
    }

    // Sets the View to fragment_search_profile.xml (only contains the RecyclerView)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_tags, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));

        // Depending on if a search term exists, either search or load all tags
        if (searchTerm.isEmpty()){
            new GetTagsAsync().execute();
        } else{
            new SearchTagsAsync().execute();
        }

        return view;
    }

    // Async task for searching tags based on a search term
    private class SearchTagsAsync extends AsyncTask<Void, Void, List<TagModel>> {
        @Override
        protected List<TagModel> doInBackground(Void... voids) {
            List<TagModel> tagsList = null;
            try {
                TagClient  client= SPWebApiRepository.getTagClient();
                tagsList = client.searchTags(searchTerm);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return tagsList;
        }

        @Override
        protected void onPostExecute(List<TagModel> tagList) {
            if (tagList == null) {
                tagList = new ArrayList<>();
            }
            tags.clear();
            tags.addAll(tagList);
            setupRecyclerView();
        }
    }

    // Async task for getting all tags (no search term)
    private class GetTagsAsync extends AsyncTask<Void, Void, List<TagModel>> {
        @Override
        protected List<TagModel> doInBackground(Void... voids) {
            List<TagModel> tagsList = null;
            try {
                TagClient  client= SPWebApiRepository.getTagClient();
                tagsList = client.getTags();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return tagsList;
        }

        @Override
        protected void onPostExecute(List<TagModel> tagList) {
            if (tagList == null) {
                tagList = new ArrayList<>();
            }
            tags.clear();
            tags.addAll(tagList);
            setupRecyclerView();
        }
    }


    // Sets the RecyclerView by sending through a List of all UserProfileModelLite
    private void setupRecyclerView() {
        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(new SearchTagsAdapter(tags));
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
