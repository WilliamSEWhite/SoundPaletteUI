package com.soundpaletteui.Activities.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Activities.MainScreenActivity;
import com.soundpaletteui.Infrastructure.Adapters.TagSelectAdapter;
import com.soundpaletteui.SPApiServices.ApiClients.TagClient;
import com.soundpaletteui.Infrastructure.Models.TagModel;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class RegisterTagsActivity extends AppCompatActivity {

    AppSettings appSettings;
    UserModel user;
    TagClient tagClient;
    private RecyclerView recyclerView;
    private TagSelectAdapter adapter;
    private List<TagModel> tagList;
    private FrameLayout frameSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_tags);
        initComponents();
        /*EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/
    }

    /** initialize UI components */
    private void initComponents() {
        appSettings = AppSettings.getInstance();
        user = AppSettings.getInstance().getUser();
        tagClient = SPWebApiRepository.getInstance().getTagClient();
        tagList = new ArrayList<>();

        recyclerView = findViewById(R.id.tagList);
        frameSave = findViewById(R.id.frame_save);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getTags();

        frameSave.setOnClickListener(v -> saveSelectedTags());
    }

    /** save the selected tags to the user profile */
    private void saveSelectedTags() {
        if(adapter == null) {
            Toast.makeText(this, "No tags were selected", Toast.LENGTH_SHORT).show();
            return;
        }
        List<TagModel> selectedTags = new ArrayList<>();
        for(TagModel tag : tagList) {
            if(tag.isSelected()) {
                selectedTags.add(tag);
            }
        }
        new Thread(() -> {
            try {
                Response<ResponseBody> response = tagClient.updateTags(user.getUserId(), selectedTags);
                runOnUiThread(() -> {
                    if(response.isSuccessful()) {
                        Toast.makeText(RegisterTagsActivity.this, "Tags saved successfully",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        System.out.println("TagRegister: " + response.body());
                        Toast.makeText(RegisterTagsActivity.this, "Failed to save tags",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                Toast.makeText(RegisterTagsActivity.this, "Error saving tags",
                        Toast.LENGTH_SHORT).show();
                throw new RuntimeException(e);
            }
        }).start();
        Intent intent = new Intent(RegisterTagsActivity.this, MainScreenActivity.class);
        // Pass user ID to the next activity
        intent.putExtra("userId", user.getUserId());
        startActivity(intent);
        finish();
    }

    /** get the tags to populate the recycler view */
    private void getTags() {
        new Thread(() -> {
            try {
                List<TagModel> tags = tagClient.getTags();
                runOnUiThread(() -> {
                    tagList = tags;
                    adapter = new TagSelectAdapter((ArrayList<TagModel>) tagList, RegisterTagsActivity.this);
                    recyclerView.setAdapter(adapter);
                });
            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(RegisterTagsActivity.this, "Error fetching tags", Toast.LENGTH_SHORT).show());
                throw new RuntimeException(e);
            }
        }).start();
    }
}