package com.soundpaletteui.Activities.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Activities.MainScreenActivity;
import com.soundpaletteui.Infrastructure.Adapters.TagSelectAdapter;
import com.soundpaletteui.Infrastructure.Models.TagModel;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.Infrastructure.Utilities.DarkModePreferences;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import com.soundpaletteui.R;
import com.soundpaletteui.SPApiServices.ApiClients.TagClient;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.EmojiBackgroundView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

// Handles letting the user select tags they are interested in after registering.
// Displays all available tags, allows the user to select them, and saves the selections.
public class RegisterTagsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TagSelectAdapter adapter;
    private List<TagModel> tagList;
    private FrameLayout frameSave;
    private AppSettings appSettings;
    private UserModel user;
    private TagClient tagClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_tags);

        // Apply gradient + emoji background
        View root = findViewById(R.id.root_layout);
        boolean isDark = DarkModePreferences.isDarkModeEnabled(this);
        UISettings.applyBrightnessGradientBackground(root, 280f, isDark);

        EmojiBackgroundView emojiBg = findViewById(R.id.emojiBackground);
        emojiBg.setPatternType(EmojiBackgroundView.PATTERN_SPIRAL);
        emojiBg.setAlpha(0.65f);

        initComponents();
    }

    // Initializes views, adapters, and listeners
    private void initComponents() {
        appSettings = AppSettings.getInstance();
        user = appSettings.getUser();
        tagClient = SPWebApiRepository.getInstance().getTagClient();
        tagList = new ArrayList<>();

        recyclerView = findViewById(R.id.tagList);
        frameSave    = findViewById(R.id.frame_save);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getTags();

        frameSave.setOnClickListener(v -> saveSelectedTags());
    }

    // Fetches all available tags from the server
    private void getTags() {
        new Thread(() -> {
            try {
                List<TagModel> tags = tagClient.getTags();
                runOnUiThread(() -> {
                    tagList = tags;
                    adapter = new TagSelectAdapter(new ArrayList<>(tagList), this);
                    recyclerView.setAdapter(adapter);
                });
            } catch (IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Error fetching tags", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    // Saves the tags the user selected to the server
    private void saveSelectedTags() {
        if (adapter == null) {
            Toast.makeText(this, "No tags were selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Collect all the selected tags
        List<TagModel> selected = new ArrayList<>();
        for (TagModel t : tagList) {
            if (t.isSelected()) selected.add(t);
        }

        // Save the selected tags
        new Thread(() -> {
            try {
                Response<ResponseBody> resp =
                        tagClient.updateTags(user.getUserId(), selected);
                runOnUiThread(() -> {
                    if (resp.isSuccessful()) {
                        Toast.makeText(this,
                                "Tags saved successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this,
                                "Failed to save tags", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(this,
                                "Error saving tags", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();

        // Move to the main screen after saving
        Intent i = new Intent(this, MainScreenActivity.class);
        i.putExtra("userId", user.getUserId());
        startActivity(i);
        finish();
    }
}
