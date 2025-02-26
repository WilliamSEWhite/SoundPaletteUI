package com.soundpaletteui.Activities.Posts;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Infrastructure.ApiClients.PostClient;
import com.soundpaletteui.Infrastructure.Models.PostContentModel;
import com.soundpaletteui.Infrastructure.Models.PostModel;
import com.soundpaletteui.Infrastructure.Models.TagModel;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.R;
import com.soundpaletteui.UISettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class PostFragment extends Fragment {
    private static final String ARG_BASE_HUE = "base_hue";
    private static final String ARG_USER_ID = "user_id";
    private static final String TAG = "PostFragment";

    private float baseHue = -1f;
    private int userId;
    private ArrayList<PostModel> allPosts = new ArrayList<>();
    private RecyclerView recyclerView;

    public static PostFragment newInstance(int id) {
        return newInstance(id, -1f);
    }

    public static PostFragment newInstance(int id, float baseHue) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, id);
        args.putFloat(ARG_BASE_HUE, baseHue);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt(ARG_USER_ID, -1);
            baseHue = getArguments().getFloat(ARG_BASE_HUE, -1f);
        }
    }

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

    private class GetPostsTask extends AsyncTask<Void, Void, List<PostModel>> {
        @Override
        protected List<PostModel> doInBackground(Void... voids) {
            List<PostModel> dummyPosts = new ArrayList<>();

            dummyPosts.add(new PostModel(1,  "new lyrics in the works! just need some good vocals…", null, new PostContentModel("Sharing some thoughts on my latest sound exploration"), new Date(), "Username1", 1));
            dummyPosts.add(new PostModel(1,  "Finished my latest track!", null, new PostContentModel("image02.png"), new Date(), "Username1", 3));
            dummyPosts.add(new PostModel(1,  "What my Friday nights are looking like..", null, new PostContentModel( "Excited to finally share this track with everyone!"), new Date(), "Username1", 1));
            dummyPosts.add(new PostModel(2,  "Trying out a new genre today!", null, new PostContentModel( "image07.png"), new Date(), "Username2", 3));
            dummyPosts.add(new PostModel(2,  "they call this position the birds eye view I believe", null, new PostContentModel( "Trying out something new and unexpected today!"), new Date(), "Username2", 1));
            dummyPosts.add(new PostModel(2,  "Inspired by nature", null, new PostContentModel( "image09.png"), new Date(), "Username2", 3));
            dummyPosts.add(new PostModel(3,  "Sunday Funday!", null, new PostContentModel( "Nature always inspires my melodies"), new Date(), "Username3", 1));
            dummyPosts.add(new PostModel(3,  "Here’s a snippet from my next album.", null, new PostContentModel( "image14.png"), new Date(), "Username3", 3));
            dummyPosts.add(new PostModel(3,  "just my morning commute guys…", null, new PostContentModel( "A sneak peek at my upcoming album. Hope you like it!"), new Date(), "Username3", 1));
            dummyPosts.add(new PostModel(1,  "Collab opportunity for vocalists!", null, new PostContentModel( "image18.png"), new Date(), "Username1", 3));
            dummyPosts.add(new PostModel(1,  "inspiration.", null, new PostContentModel( "Looking for a vocalist to collaborate with on my next track."), new Date(), "Username1", 1));
            dummyPosts.add(new PostModel(1,  "A little jazz influence in this one.", null, new PostContentModel( "image21.png"), new Date(), "Username1", 3));
            dummyPosts.add(new PostModel(2,  "writing. dreaming. thinking.", null, new PostContentModel( "Blending jazz influences into my latest piece"), new Date(), "Username2", 1));

            return dummyPosts;
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



//    private class GetPostsTask extends AsyncTask<Void, Void, List<PostModel>> {
//        @Override
//        protected List<PostModel> doInBackground(Void... voids) {
//            try {
//                PostClient client = SPWebApiRepository.getInstance().getPostClient();
//                List<PostModel> posts = client.getPosts();
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
//
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

}
