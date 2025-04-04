package com.soundpaletteui.Activities.Posts;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Activities.Home.HomeFragment;
import com.soundpaletteui.Activities.Profile.ProfileFragment;
import com.soundpaletteui.Infrastructure.Adapters.CountrySelectAdapter;
import com.soundpaletteui.Infrastructure.Adapters.MainContentAdapter;
import com.soundpaletteui.Infrastructure.Adapters.TagRowAdapter;
import com.soundpaletteui.Infrastructure.Adapters.UserTagAdapter;
import com.soundpaletteui.Infrastructure.ApiClients.PostClient;
import com.soundpaletteui.Infrastructure.ApiClients.TagClient;
import com.soundpaletteui.Infrastructure.Models.LocationModel;
import com.soundpaletteui.Infrastructure.Models.NewPostModel;
import com.soundpaletteui.Infrastructure.Models.PostTypeModel;
import com.soundpaletteui.Infrastructure.Models.TagModel;
import com.soundpaletteui.Infrastructure.Models.UserInfoModel;
import com.soundpaletteui.Infrastructure.Models.UserModel;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import com.soundpaletteui.Infrastructure.Utilities.DarkModePreferences;
import com.soundpaletteui.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreatePostFragment extends Fragment {

    private int postType;
    private UserModel user;
    private List<TagModel> tags;
    private ArrayList<TagModel> selectedTags = new ArrayList<>();
    private MainContentAdapter mainContentAdapter;
    private UserTagAdapter adapter;
    private RecyclerView recyclerView;

    public CreatePostFragment() {
        // Required empty public constructor
    }

    public static CreatePostFragment newInstance(int postType) {
        CreatePostFragment fragment = new CreatePostFragment();
        Bundle args = new Bundle();
        args.putInt("Post_Type", postType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postType = getArguments().getInt("Post_Type", -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_create_post, container, false);
        // Apply dark mode gradient background
        boolean isDarkMode = DarkModePreferences.isDarkModeEnabled(rootView.getContext());
        UISettings.applyBrightnessGradientBackground(rootView, 0f, isDarkMode);

        initView(rootView);
        Button btnAddTags = rootView.findViewById(R.id.addTags);
        btnAddTags.setOnClickListener(v -> addTags());
        Button btnSavePost = rootView.findViewById(R.id.btnSave);
        btnSavePost.setOnClickListener(v -> savePost());
        user = AppSettings.getInstance().getUser();
        recyclerView = rootView.findViewById(R.id.userProfileTags);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        return rootView;
    }

    private void initView(View rootView){
        switch (postType){
            case 1:
                TextPostContentFragment textPostContent = TextPostContentFragment.newInstance();
                replaceFragment(textPostContent, "TEXT_POST_CONTENT_FRAGMENT");
                break;
            case 2:

                break;
            case 3:

                break;
        }
        new GetTagsAsync().execute();
    }

    private void addTags() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View addTagsDialog = getLayoutInflater().inflate(R.layout.dialog_tag_select, null);
        builder.setView(addTagsDialog);

        ListView tagListView = addTagsDialog.findViewById(R.id.tagsList);
        tagListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        TagRowAdapter tagRowAdapter = new TagRowAdapter(getActivity(), (ArrayList<TagModel>) tags);
        tagListView.setAdapter(tagRowAdapter);

        tagListView.setOnItemClickListener((parent, view, position, id) -> {
            TagModel tag = (TagModel) tagListView.getItemAtPosition(position);
            if(selectedTags.contains(tag)){
                selectedTags.remove(tag);
            } else {
                selectedTags.add(tag);
            }
        });
        builder.setPositiveButton("Save", (dialog, which) -> {
            dialog.dismiss();
            refreshTagList();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(R.drawable.rounded_corners);
        }
    }

    private void refreshTagList() {
        if (selectedTags != null && !selectedTags.isEmpty()) {
            adapter = new UserTagAdapter(selectedTags, getContext());
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            Log.d("CreatePostFragment", "No tags received or empty list");
        }
    }

    private void savePost(){
        View v = getView();
        int userId = user.getUserId();
        String caption = ((EditText)v.findViewById(R.id.caption)).getText().toString();
        boolean isPremium = false;
        boolean isMature = false;
        Date createdDate = new Date();
        Date publishDate = new Date();
        String postTextContent = "test";
        switch (postType){
            case 1:
                postTextContent = getTextContent(v);
                break;
        }
        NewPostModel newPost = new NewPostModel(userId, postType, caption, isPremium, isMature, createdDate, publishDate, selectedTags, postTextContent );
        new MakePostAsync().execute(newPost);
    }

    private String getTextContent(View v){
        TextPostContentFragment frag = (TextPostContentFragment) getChildFragmentManager().findFragmentById(R.id.postContentShell);
        return frag.getContent();
    }

    private void replaceFragment(Fragment fragment, String tag){
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.postContentShell, fragment, tag);
        transaction.commit();
    }

    private void finishPost(){
        replaceMainFragment(new HomeFragment(), "HOME_FRAGMENT");
    }

    private void replaceMainFragment(Fragment new_fragment, String tag) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        Navigation.replaceFragment(fragmentManager, new_fragment, tag, R.id.mainScreenFrame);
    }

    private class GetTagsAsync extends AsyncTask<Void,Void, Void> {
        protected Void doInBackground(Void... d) {
            try {
                TagClient client = SPWebApiRepository.getInstance().getTagClient();
                tags = client.getTags();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
        protected void onPostExecute(Void v) {

        }
    }

    private class MakePostAsync extends AsyncTask<NewPostModel,Void, Void> {
        protected Void doInBackground(NewPostModel... d) {
            try {
                NewPostModel newPost = d[0];
                PostClient client = SPWebApiRepository.getInstance().getPostClient();
                client.makePost(newPost);
            } catch (IOException e) {
                Toast.makeText(requireContext(), "Error making post", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
        protected void onPostExecute(Void v) {
            finishPost();
        }
    }
}
