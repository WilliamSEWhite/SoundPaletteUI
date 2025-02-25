package com.soundpaletteui.Activities.Posts;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Activities.Home.HomeFragment;
import com.soundpaletteui.Activities.Profile.ProfileFragment;
import com.soundpaletteui.Activities.Profile.Register;
import com.soundpaletteui.Infrastructure.Adapters.CountrySelectAdapter;
import com.soundpaletteui.Infrastructure.Adapters.MainContentAdapter;
import com.soundpaletteui.Infrastructure.Adapters.TagRowAdapter;
import com.soundpaletteui.Infrastructure.ApiClients.TagClient;
import com.soundpaletteui.Infrastructure.Models.LocationModel;
import com.soundpaletteui.Infrastructure.Models.NewPostModel;
import com.soundpaletteui.Infrastructure.Models.TagModel;
import com.soundpaletteui.Infrastructure.Models.UserInfoModel;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

public class CreatePostFragment extends Fragment {

    private int postType;

    private List<TagModel> tags;
    private ArrayList<TagModel> selectedTags = new ArrayList<TagModel>();
    private MainContentAdapter mainContentAdapter;

    public CreatePostFragment() {
        // Required empty public constructor
    }

    public static CreatePostFragment newInstance(int postType) {
        CreatePostFragment fragment = new CreatePostFragment();
        Bundle args = new Bundle();
        args.putInt("Post_Type", postType); // Add userId to the Bundle
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            postType = getArguments().getInt("Post_Type", -1); // Default to -1 if not found
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_create_post, container, false);
        initView();
        Button btnAddTags = rootView.findViewById(R.id.addTags);

        btnAddTags.setOnClickListener(v -> {
            addTags();
        });
        Button btnSavePost = rootView.findViewById(R.id.btnSave);

        btnSavePost.setOnClickListener(v -> {
            savePost();
        });
        return rootView;

    }

    private void initView(){
                switch (postType){
            case 1:
                TextPostContentFragment textPostContent = TextPostContentFragment.newInstance();
                replaceFragment(textPostContent);
                break;
            case 2:
                break;
            case 3:
                break;
        }


        new GetTagsAsync().execute();

    }

    private void addTags() {
        //create dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //create dialog with custom
        final View addTagsDialog = getLayoutInflater().inflate(R.layout.dialog_tag_select, null);
        builder.setView(addTagsDialog);

        ListView tagListView = addTagsDialog.findViewById(R.id.tagsList);
        tagListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        TagRowAdapter tagRowAdapter = new TagRowAdapter(getActivity(), (ArrayList<TagModel>) tags);
        tagListView.setAdapter(tagRowAdapter);

        tagListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                TagModel tag = (TagModel) tagListView.getItemAtPosition(position);
                if(selectedTags.contains(tag)){
                    selectedTags.remove(tag);
                }
                else{
                    selectedTags.add(tag);
                }
            }
        });
        builder.setPositiveButton("Save", (dialog, which) -> {

            dialog.dismiss();
        });//on delete, open delete dialog

        //show dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // add custom layout styling from drawable.rounded_corners
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(R.drawable.rounded_corners);
        }


    }//end openTeam

    private void savePost(){
        NewPostModel newPost = new NewPostModel();
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.postContentShell, fragment);
        transaction.commit();
    }
    private class GetTagsAsync extends AsyncTask<Void,Void, Void> {
        protected Void doInBackground(Void... d) {
            System.out.println("UpdateUserInfoAsync");
            try {
                TagClient client = SPWebApiRepository.getInstance().getTagClient();
                tags = client.getTags();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }//end doInBackground

        protected void onPostExecute(Void v) {

        }//end onPostExecute
    }

}