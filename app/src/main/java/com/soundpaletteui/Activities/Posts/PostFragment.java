package com.soundpaletteui.Activities.Posts;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import com.soundpaletteui.Infrastructure.ApiClients.PostClient;
import com.soundpaletteui.Infrastructure.Models.PostModel;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.R;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PostFragment extends Fragment {
    private static final String ARG_ALGO_TYPE = "AlgorithmType";
    private static final String ARG_SEARCH_TERM = "SearchTerm";
    private static final String ARG_BASE_HUE = "base_hue";
    private static final String TAG = "PostFragment";
    private float baseHue = -1f;
    private ArrayList<PostModel> allPosts = new ArrayList<>();
    private RecyclerView recyclerView;
    private final PostClient postClient = SPWebApiRepository.getInstance().getPostClient();
    private String algoType;
    private String searchTerm;

    // New Instance of a PostFragment with algorithmType only
    public static PostFragment newInstance(String algorithmType) {
        return newInstance(algorithmType, null, -1f);
    }

    // New Instance of a PostFragment with algorithmType and searchTerm (may also be a userId)
    public static PostFragment newInstance(String algorithmType, String searchTerm) {
        return newInstance(algorithmType, searchTerm, -1f);
    }

    // New Instance of a PostFragment with algorithmType, searchTerm and baseHue
    public static PostFragment newInstance(String algorithmType, String searchTerm, float baseHue) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ALGO_TYPE, algorithmType);
        args.putString(ARG_SEARCH_TERM, searchTerm);
        args.putFloat(ARG_BASE_HUE, baseHue);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.algoType = getArguments().getString(ARG_ALGO_TYPE);
            this.searchTerm = getArguments().getString(ARG_SEARCH_TERM);
            this.baseHue = getArguments().getFloat(ARG_BASE_HUE, -1f);
        }
    }

    // Sets the View to fragment_post.xml (only contains the RecyclerView)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        if (baseHue >= 0f) {
            UISettings.applyBrightnessGradientBackground(view, baseHue);
        }
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));

        new GetPostsTask().execute();
        return view;
    }

    // Gets Posts from the client
    private class GetPostsTask extends AsyncTask<Void, Void, List<PostModel>> {
        @Override
        protected List<PostModel> doInBackground(Void... voids) {
            List<PostModel> posts = null;
            try {
                switch (algoType) {
                    case "popular":
                        posts = postClient.getPosts();
                        break;
                    case "user":
                        posts = postClient.getPostsForUser();
                        break;
                    case "saved":
                        posts = postClient.getSavedPostsForUser();
                        break;
                    case "username":
                        posts = postClient.getPostsForUsername(searchTerm);
                        break;
                    case "following":
                        posts = postClient.getFollowingPosts();
                        break;
                    default:
                        // fallback if needed
                        posts = postClient.getPosts();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error fetching posts", e);
                posts = new ArrayList<>();
            }

            return posts;
        }

        @Override
        protected void onPostExecute(List<PostModel> posts) {
            if (posts == null) {
                Log.w(TAG, "Received null posts list, initializing empty list");
                posts = new ArrayList<>();
            }
            Log.d(TAG, "Fetched dummy posts: " + posts.size());
            allPosts.clear();
            allPosts.addAll(posts);
            setupRecyclerView();
        }
    }

    // Sets the RecyclerView by sending through a List of all PostModels
    private void setupRecyclerView() {
        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(new PostAdapter(allPosts));
        } else {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    private void setRandomGradientBackground(View rootView) {
        Random random = new Random();
        int alpha = 128 + random.nextInt(128);
        int red   = random.nextInt(256);
        int green = random.nextInt(256);
        int blue  = random.nextInt(256);
        int randomColor = Color.argb(alpha, red, green, blue);
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.WHITE, randomColor}
        );
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        rootView.setBackground(gradientDrawable);
    }

// Untested getPostsTask that should connect to the API Server
// Note: PostFragment takes an algorithmType and searchTerm (opt) parameters
//    private class GetPostsTask extends AsyncTask<Void, Void, List<PostModel>> {
//        @Override
//        protected List<PostModel> doInBackground(Void... voids) {
//            try {
//                PostClient client = SPWebApiRepository.getInstance().getPostClient();
//                List<PostModel> posts;
//
//                String algorithmType = getArguments().getString(ARG_ALGO_TYPE, nul);
//                String searchTerm = getArguments().getString(ARG_SEARCH_TERM, null);
//
//                switch (algorithmType) {
//                    case "user":          //Posts by the current User
//                        posts = client.getUsersPosts(userId);
//                        break;
//                    case "following":     //All posts based on User's followers
//                        posts = client.getFollowingPosts(userId);
//                        break;
//                    case "saved":         //All saved posts
//                        posts = client.getSavedPosts(userId);
//                        break;
//                    case "new":           //All new posts
//                        posts = client.getNewestPosts();
//                        break;
//                    case "popular":       //All popular posts
//                        posts = client.getPopularPosts();
//                        break;
//                    case "trending":      //All trending posts
//                        posts = client.getTrendingPosts();
//                        break;
//                    case "searchTerm":    //Posts based on search term
//                        posts = client.getSearchTermPosts(ARG_SEARCH_TERM);
//                        break;
//                    default:              //All posts
//                        posts = client.getAllPosts();
//                        break;
//                }
////    private class GetPostsTask extends AsyncTask<Void, Void, List<PostModel>> {
////        @Override
////        protected List<PostModel> doInBackground(Void... voids) {
////            try {
////                PostClient client = SPWebApiRepository.getInstance().getPostClient();
////                List<PostModel> posts;
////
////                String algorithmType = getArguments().getString(ARG_ALGO_TYPE, nul);
////                String searchTerm = getArguments().getString(ARG_SEARCH_TERM, null);
////
////                switch (algorithmType) {
////                    case "user":          //Posts by the current User
////                        posts = client.getUsersPosts(userId);
////                        break;
////                    case "following":     //All posts based on User's followers
////                        posts = client.getFollowingPosts(userId);
////                        break;
////                    case "saved":         //All saved posts
////                        posts = client.getSavedPosts(userId);
////                        break;
////                    case "new":           //All new posts
////                        posts = client.getNewestPosts();
////                        break;
////                    case "popular":       //All popular posts
////                        posts = client.getPopularPosts();
////                        break;
////                    case "trending":      //All trending posts
////                        posts = client.getTrendingPosts();
////                        break;
////                    case "searchTerm":    //Posts based on search term
////                        posts = client.getSearchTermPosts(ARG_SEARCH_TERM);
////                        break;
////                    default:              //All posts
////                        posts = client.getAllPosts();
////                        break;
////                }
////
////                return (posts != null) ? posts : new ArrayList<>();
////            } catch (IOException e) {
////                Log.e(TAG, "Error fetching posts", e);
////                return new ArrayList<>();
////            }
////        }
////
//                return (posts != null) ? posts : new ArrayList<>();
//            } catch (IOException e) {
//                Log.e(TAG, "Error fetching posts", e);
//                return new ArrayList<>();
//            }
//        }
//
//        @Override
//        protected void onPostExecute(List<PostModel> posts) {
//            if (posts == null) {
//                Log.w(TAG, "Received null posts list, initializing empty list");
//                posts = new ArrayList<>();
//            }
//            Log.d(TAG, "Fetched posts: " + posts.size());
//            allPosts.clear();
//            allPosts.addAll(posts);
//            setupRecyclerView();
//        }
//    }

}
