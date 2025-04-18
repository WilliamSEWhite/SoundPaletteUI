package com.soundpaletteui.Activities.Posts;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.type.DateTime;
import com.google.type.DateTimeOrBuilder;
import com.soundpaletteui.SPApiServices.ApiClients.PostClient;
import com.soundpaletteui.Infrastructure.Models.Post.PostModel;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;
import com.soundpaletteui.R;
import com.soundpaletteui.Infrastructure.Utilities.MediaPlayerManager;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PostFragment extends Fragment {
    private static final String ARG_ALGO_TYPE = "AlgorithmType";
    private static final String ARG_SEARCH_TERM = "SearchTerm";
    private static final String ARG_BASE_HUE = "base_hue";
    private static final String ARG_SHOW_EDIT = "showEditButton";
    private static final String TAG = "PostFragment";

    private boolean showEditButton;
    private float baseHue = -1f;
    private ArrayList<PostModel> allPosts = new ArrayList<>();
    private RecyclerView recyclerView;
    private final PostClient postClient = SPWebApiRepository.getInstance().getPostClient();
    private String algoType;
    private String searchTerm;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView noPosts;
    private DateTime loadDate;
    private int page = 0;
    private int totalItemCount;
    private ProgressBar loading;
    private boolean isLoading = false;
    int count = 0;
    private NestedScrollView nestedSV;


    // New Instance of a PostFragment with algorithmType only
    public static PostFragment newInstance(String algorithmType) {
        return newInstance(algorithmType, null, false, -1f);
    }

    // New Instance of a PostFragment with algorithmType and searchTerm (may also be a userId)
    public static PostFragment newInstance(String algorithmType, String searchTerm) {
        return newInstance(algorithmType, searchTerm, false,-1f);
    }


    // New Instance of a PostFragment with algorithmType, searchTerm, and  showEditButton
    public static PostFragment newInstance(String algorithmType, String searchTerm, boolean showEditButton) {
        return newInstance(algorithmType, searchTerm, showEditButton,-1f);
    }

    // New Instance of a PostFragment with algorithmType, searchTerm, showEditButton and baseHue
    public static PostFragment newInstance(String algorithmType, String searchTerm, boolean showEditButton, float baseHue) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ALGO_TYPE, algorithmType);
        args.putString(ARG_SEARCH_TERM, searchTerm);
        args.putBoolean(ARG_SHOW_EDIT, showEditButton);
        args.putFloat(ARG_BASE_HUE, baseHue);
        fragment.setArguments(args);

        Log.d("SHOW EDIT BUTTON ON POST FRAGMENT", String.valueOf(showEditButton));
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.algoType = getArguments().getString(ARG_ALGO_TYPE);
            this.searchTerm = getArguments().getString(ARG_SEARCH_TERM);
            this.baseHue = getArguments().getFloat(ARG_BASE_HUE, -1f);
            this.showEditButton = getArguments().getBoolean(ARG_SHOW_EDIT, false);
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
//            UISettings.applyBrightnessGradientBackground(view, baseHue);
        }
        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        noPosts = view.findViewById(R.id.list_empty);
//        loading = view.findViewById(R.id.loading);
        nestedSV = view.findViewById(R.id.idNestedSV);

        nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
//                    loading.setVisibility(View.VISIBLE);
                    page++;
                    new GetPostsTask().execute();
                }
            }
        });

        recyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool()); // Add this line
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));

        SwipeRefreshLayout.OnRefreshListener swipeRefreshListner = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                new GetPostsTask().execute();
            }
        };


        // SetOnRefreshListener on SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(swipeRefreshListner);
        swipeRefreshLayout.post(new Runnable() {
            @Override public void run() {
                swipeRefreshLayout.setRefreshing(true);
                // directly call onRefresh() method
                swipeRefreshListner.onRefresh();
            }
        });

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
                        posts = postClient.getPosts(page);
                        break;
                    case "user":
                        posts = postClient.getPostsForUser(page);
                        break;
                    case "saved":
                        posts = postClient.getSavedPostsForUser(page);
                        break;
                    case "username":
                        posts = postClient.getPostsForUsername(searchTerm, page);
                        break;
                    case "following":
                        posts = postClient.getFollowingPosts(page);
                        break;
                    case "trending":
                        posts = postClient.getTrendingPosts(searchTerm, page);
                        break;
                    case "postusertags":
                        posts = postClient.getTaggedPostsForUsername(searchTerm, page);
                        break;
                    case "search":
                        posts = postClient.searchPosts(searchTerm, page);
                        break;
                    case "tag":
                        posts = postClient.getPostsByTag(Integer.parseInt(searchTerm), page);
                        break;
                    default:
                        // fallback if needed
                        posts = postClient.getPosts(page);
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
            if(page == 1)
                allPosts.clear();

            allPosts.addAll(posts);

            setupRecyclerView();
        }
    }

    // Pauses the media player when user leaves the fragment
    @Override
    public void onPause() {
        super.onPause();
        MediaPlayerManager.getInstance().release();
    }


    // Sets the RecyclerView by sending through a List of all PostModels
    private void setupRecyclerView() {
        if(allPosts.isEmpty()){
            swipeRefreshLayout.setVisibility(GONE);
            noPosts.setVisibility(VISIBLE);
        }
        else{
            swipeRefreshLayout.setVisibility(VISIBLE);
            noPosts.setVisibility(GONE);
        }
        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(new PostAdapter(allPosts, showEditButton));

        } else {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
        swipeRefreshLayout.setRefreshing(false);

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

}
