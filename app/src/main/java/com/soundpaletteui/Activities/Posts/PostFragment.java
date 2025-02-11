package com.soundpaletteui.Activities.Posts;

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

import com.soundpaletteui.R;

import java.util.ArrayList;
import java.util.List;

public class PostFragment extends Fragment {
    private static final String ARG_IMAGES = "array_images";
    private static final String ARG_CAPTIONS = "array_captions";
    private int arrayImagesId;
    private int arrayCaptionId;

    public static PostFragment newInstance(int userId) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_IMAGES, getImagesArrayResourceId(String.valueOf(userId)));
        args.putInt(ARG_CAPTIONS, getCaptionArrayResourceId(String.valueOf(userId)));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            arrayImagesId = getArguments().getInt(ARG_IMAGES, 0);
            arrayCaptionId = getArguments().getInt(ARG_CAPTIONS, 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        List<String> imagePaths = loadImageArray();
        List<String> captions = loadCaptionsArray();

        PostAdapter adapter = new PostAdapter(imagePaths, captions);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<String> loadImageArray() {
        List<String> images = new ArrayList<>();
        if (getContext() != null && arrayImagesId != 0) {
            String[] imageNames = getResources().getStringArray(arrayImagesId);
            for (String name : imageNames) {
                images.add("android.resource://" + requireContext().getPackageName() + "/drawable/" + name.replace(".png", ""));
            }
        } else {
            Toast.makeText(requireContext(), "Invalid image array resource", Toast.LENGTH_SHORT).show();
        }
        return images;
    }

    private List<String> loadCaptionsArray() {
        List<String> captions = new ArrayList<>();
        if (getContext() != null && arrayCaptionId != 0) {
            String[] captionArray = getResources().getStringArray(arrayCaptionId);
            for (String caption : captionArray) {
                captions.add(caption);
            }
        }
        return captions;
    }

    private static int getImagesArrayResourceId(String userId) {
        switch (userId) {
            case "1": return R.array.user1_images;
            case "2": return R.array.user2_images;
            case "3": return R.array.user3_images;
            case "4": return R.array.user4_images;
            case "5": return R.array.user5_images;
            case "6": return R.array.user6_images;
            case "7": return R.array.art_images;
            case "8": return R.array.music_images;
            case "9": return R.array.random_images;
            case "10": return R.array.all_images;
            case "11": return R.array.allrandom1_images;
            case "12": return R.array.allrandom2_images;
            case "13": return R.array.allrandom3_images;
            case "14": return R.array.allrandom4_images;
            case "15": return R.array.allrandom5_images;
            default: return 10;
        }
    }

    private static int getCaptionArrayResourceId(String userId) {
        switch (userId) {
            case "1": return R.array.user1_captions;
            case "2": return R.array.user2_captions;
            case "3": return R.array.user3_captions;
            case "4": return R.array.user4_captions;
            case "5": return R.array.user5_captions;
            case "6": return R.array.user6_captions;
            case "7": return R.array.art_captions;
            case "8": return R.array.music_captions;
            case "9": return R.array.random_captions;
            case "10": return R.array.all_captions;
            case "11": return R.array.allrandom1_captions;
            case "12": return R.array.allrandom2_captions;
            case "13": return R.array.allrandom3_captions;
            case "14": return R.array.allrandom4_captions;
            case "15": return R.array.allrandom5_captions;
            default: return 10;
        }
    }
}
