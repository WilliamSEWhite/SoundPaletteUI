package com.soundpaletteui.Activities.Posts;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Activities.Posts.PostAdapter;
import com.soundpaletteui.R;

import java.util.ArrayList;
import java.util.List;

public class PostFragment extends Fragment {
    private static final String ARG_ARRAY_NAME = "array_name";
    private int arrayResourceId;

    public static PostFragment newInstance(int arrayResId) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ARRAY_NAME, arrayResId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            arrayResourceId = getArguments().getInt(ARG_ARRAY_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        List<String> imagePaths = loadImageArray();
        PostAdapter adapter = new PostAdapter(imagePaths);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<String> loadImageArray() {
        List<String> images = new ArrayList<>();
        if (getContext() != null) {
            String[] imageNames = getResources().getStringArray(arrayResourceId);
            for (String name : imageNames) {
                images.add("android.resource://" + requireContext().getPackageName() + "/drawable/" + name.replace(".png", ""));
                //images.add("file:///android_res/drawable/" + name);
                if (arrayResourceId == 0) {
                    Toast.makeText(requireContext(), name+ " is an invalid image array resource", Toast.LENGTH_SHORT).show();
                    return images;
                }
            }
        }
        return images;
    }
}
