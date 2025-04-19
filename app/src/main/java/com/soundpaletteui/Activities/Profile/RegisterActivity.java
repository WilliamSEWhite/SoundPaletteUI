package com.soundpaletteui.Activities.Profile;

import static java.util.Calendar.getInstance;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.soundpaletteui.Infrastructure.Adapters.CountrySelectAdapter;
import com.soundpaletteui.Infrastructure.Models.LocationModel;
import com.soundpaletteui.Infrastructure.Models.User.UserInfoModel;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.Infrastructure.Models.User.UserProfileModel;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.Infrastructure.Utilities.DarkModePreferences;
import com.soundpaletteui.Infrastructure.Utilities.ImageUtils;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import com.soundpaletteui.R;
import com.soundpaletteui.SPApiServices.ApiClients.FileClient;
import com.soundpaletteui.SPApiServices.ApiClients.LocationClient;
import com.soundpaletteui.SPApiServices.ApiClients.UserClient;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;
import com.soundpaletteui.Views.EmojiBackgroundView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class RegisterActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private final AppSettings appSettings = AppSettings.getInstance();

    private EditText txtEmail;
    private EditText txtPhone;
    private Date dob;
    private ImageView profileImage;
    private Uri imageUri;
    private Spinner locationSpinner;
    private ArrayList<LocationModel> countries;
    private UserClient userClient;
    private int userId;
    private UserModel user;
    private UserProfileModel userProfileModel;
    private FrameLayout frameSave;
    private GifImageView gifSave;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private FileClient fileClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // — Apply emoji + brightness gradient exactly like Search/Create‑Post
        View root = findViewById(R.id.root_layout);
        boolean isDark = DarkModePreferences.isDarkModeEnabled(this);
        UISettings.applyBrightnessGradientBackground(root, 280f, isDark);

        EmojiBackgroundView emojiBg = findViewById(R.id.emojiBackground);
        emojiBg.setPatternType(EmojiBackgroundView.PATTERN_SPIRAL);
        emojiBg.setAlpha(0.65f);

        initComponents();

        // image picker launcher
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        displayImage(imageUri);
                    }
                }
        );
    }

    private void initComponents() {
        txtEmail       = findViewById(R.id.registerEmail);
        txtPhone       = findViewById(R.id.registerPhone);
        profileImage   = findViewById(R.id.registerProfilePicture);
        frameSave      = findViewById(R.id.frame_save);
        gifSave        = findViewById(R.id.gif_save);
        locationSpinner= findViewById(R.id.registerLocation);

        fileClient     = SPWebApiRepository.getInstance().getFileClient();
        userClient     = SPWebApiRepository.getInstance().getUserClient();
        user           = appSettings.getUser();
        userId         = getIntent().getIntExtra("userId", 0);
        userProfileModel = new UserProfileModel(user.getUserId(),
                "I haven't updated my bio yet…", "/dev/null");

        // pick DOB
        findViewById(R.id.pick_date).setOnClickListener(v -> {
            if (dob == null) dob = new Date();
            int y = dob.getYear() + 1900, m = dob.getMonth(), d = dob.getDate();
            new DatePickerDialog(this, (view, year, month, day) -> {
                dob = new Date(year-1900, month, day);
                ((TextView)findViewById(R.id.selected_date))
                        .setText(new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
                                .format(dob));
            }, y, m, d).show();
        });

        profileImage.setOnClickListener(v -> ImageUtils.pickImageFromGallery(this, pickImageLauncher));

        frameSave.setOnClickListener(v -> {
            try {
                ((GifDrawable)gifSave.getDrawable()).start();
                frameSave.postDelayed(() -> ((GifDrawable)gifSave.getDrawable()).stop(), 800);
            } catch (ClassCastException e) {
                Toast.makeText(this, "Save animation error", Toast.LENGTH_SHORT).show();
            }
            saveUserInfo();
            ImageUtils.uploadProfileImage(imageUri, user.getUserId(), fileClient, this);
        });

        // load countries
        new GetLocationsAsync().execute();
    }

    private void displayImage(Uri uri) {
        Glide.with(this)
                .load(uri != null ? uri : R.drawable.baseline_person_150)
                .placeholder(R.drawable.baseline_person_150)
                .error(R.drawable.baseline_person_150)
                .into(profileImage);
    }

    private void saveUserInfo() {
        new UpdateUserInfoAsync().execute();
        Toast.makeText(this, "User profile saved", Toast.LENGTH_SHORT).show();
    }

    private class GetLocationsAsync extends AsyncTask<Void, Void, List<LocationModel>> {
        @Override
        protected List<LocationModel> doInBackground(Void... voids) {
            try {
                return SPWebApiRepository.getInstance()
                        .getLocationClient().getLocations();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        @Override
        protected void onPostExecute(List<LocationModel> list) {
            countries = new ArrayList<>(list);
            CountrySelectAdapter adapter = new CountrySelectAdapter(
                    RegisterActivity.this,
                    android.R.layout.simple_spinner_item,
                    countries
            );
            locationSpinner.setAdapter(adapter);
            locationSpinner.setSelection(0);
        }
    }

    private class UpdateUserInfoAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String email = txtEmail.getText().toString();
                String phone = txtPhone.getText().toString();
                LocationModel loc = (LocationModel)
                        locationSpinner.getSelectedItem();

                String created = new SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()
                ).format(getInstance().getTime());

                UserInfoModel info = new UserInfoModel(
                        user.getUserId(),
                        loc.getLocationId(),
                        email, phone, dob, created
                );
                appSettings.setUser(
                        userClient.updateUserInfo(user.getUserId(), info)
                );
                userClient.updateUserProfile(userProfileModel);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            startActivity(new Intent(
                    RegisterActivity.this,
                    RegisterTagsActivity.class
            ));
            finish();
        }
    }
}
