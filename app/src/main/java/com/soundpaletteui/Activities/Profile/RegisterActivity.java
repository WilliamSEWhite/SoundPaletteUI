package com.soundpaletteui.Activities.Profile;

import static java.util.Calendar.getInstance;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.soundpaletteui.Infrastructure.Adapters.CountrySelectAdapter;

import com.soundpaletteui.SPApiServices.ApiClients.UserClient;
import com.soundpaletteui.SPApiServices.ApiClients.LocationClient;
import com.soundpaletteui.SPApiServices.ApiClients.FileClient;
import com.soundpaletteui.SPApiServices.ApiClients.LocationClient;
import com.soundpaletteui.SPApiServices.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.LocationModel;
import com.soundpaletteui.Infrastructure.Models.User.UserInfoModel;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.Infrastructure.Models.User.UserProfileModel;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.Infrastructure.Utilities.DarkModePreferences;
import com.soundpaletteui.Infrastructure.Utilities.ImageUtils;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import com.soundpaletteui.R;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

// Handles the user registration screen, where users can fill out their profile information,
// Select a profile picture, and save their account details to the database.
public class RegisterActivity extends AppCompatActivity {
    AppSettings appSettings = AppSettings.getInstance();
    private EditText txtEmail, txtPhone;
    private Uri imageUri;
    private ImageView profileImage;
    private Spinner locationSpinner;
    private ArrayList<LocationModel> countries;
    private Intent intent;
    private UserModel user;
    private Date dob;
    private UserClient userClient;
    private int userId;
    private UserProfileModel userProfileModel;
    private FrameLayout frameSave;
    private GifImageView gifSave;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private FileClient fileClient;

    // Sets up the layout and calls initialization.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Apply emoji + brightness gradient exactly like Search/Create‑Post
        View root = findViewById(R.id.root_layout);
        boolean isDark = DarkModePreferences.isDarkModeEnabled(this);
        UISettings.applyBrightnessGradientBackground(root, 280f, isDark);

        com.soundpaletteui.Infrastructure.Utilities.EmojiBackgroundView emojiBg = findViewById(R.id.emojiBackground);
        emojiBg.setPatternType(com.soundpaletteui.Infrastructure.Utilities.EmojiBackgroundView.PATTERN_SPIRAL);
        emojiBg.setAlpha(0.65f);

        initComponents();

        // launches the image picker
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        if(profileImage != null) {
                            displayImage(imageUri);
                        } else {
                            Log.e("ProfileEditFragment", "imageView is null");
                        }
                    }
                }
        );
    }

    // Initializes components in the UI
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

        // Set up the date picker for date of birth
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

        // Set up click to edit the profile image
        profileImage.setOnClickListener(v -> editProfileImage());

        // Save button click
        frameSave.setOnClickListener(v -> {
            try {
                final GifDrawable saveGif = (GifDrawable) gifSave.getDrawable();
                frameSave.getBackground().mutate().setAlpha(255);
                saveGif.start();
                new android.os.Handler().postDelayed(() -> saveGif.stop(), 800);
            } catch (ClassCastException e) {
                e.printStackTrace();
                Toast.makeText(RegisterActivity.this, "Error with Save animation", Toast.LENGTH_SHORT).show();
            }
            saveUserInfo();
            uploadProfileImage();
        });

        // Get data from previous activity
        intent = getIntent();
        userId = intent.getIntExtra("userId", 0);
        userClient = SPWebApiRepository.getInstance().getUserClient();
        getCountries();     // load countries from database
        user = AppSettings.getInstance().getUser();
        userProfileModel = new UserProfileModel(user.getUserId(), "", "/dev/null");
    }

    // Upload profile image
    private void uploadProfileImage() {
        ImageUtils.uploadProfileImage(imageUri, user.getUserId(), fileClient, this);
    }

    // Edit the profile image
    private void editProfileImage() {
        ImageUtils.pickImageFromGallery(this, pickImageLauncher);
    }

    // Loads the image
    private void loadImage() {
        Glide.with(this).load(imageUri).into(profileImage);
    }

    // Initialize Spinner with list of countries
    private void getCountries() {
        new GetLocationsAsync().execute();
    }

    // Pulls countries from the database for the spinner
    private void initCountries(){
        CountrySelectAdapter adapter = new CountrySelectAdapter(this,
                android.R.layout.simple_spinner_item,
                countries);
        locationSpinner = (Spinner) findViewById(R.id.registerLocation);
        locationSpinner.setAdapter(adapter); // Set the custom adapter to the spinner

        // You can create an anonymous listener to handle the event when is selected an spinner item
        locationSpinner.setSelection(0);                              //retain previously selected value
    }

    // Save information to database
    private void saveUserInfo() {
        new UpdateUserInfoAsync().execute();
        Toast.makeText(RegisterActivity.this, "User profile saved ", Toast.LENGTH_SHORT).show();
    }

    // Clear text fields in register activity
    private void onRegistrationComplete() {
        Intent i = new Intent(RegisterActivity.this, RegisterTagsActivity.class);
        startActivity(i);
        finish();

    }

    // Displays the image in the register screen
    private void displayImage(Uri imageUri) {
        if (imageUri != null) {
            Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.baseline_person_150)
                    .error(R.drawable.baseline_person_150)
                    .into(profileImage);
        } else {
            Glide.with(this)
                    .load(R.drawable.baseline_person_150)
                    .into(profileImage);
        }
    }

    // Gets user data
    private void getUser() {
        new GetUserAsync().execute();
    }

    // Async call to the database for the user data
    private class GetUserAsync extends AsyncTask<Void,Void, Void> {
        protected Void doInBackground(Void... d) {
            try {
                user = userClient.getUser(userId);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            //populateView();
        }
    }

    // Async call to pull countries from the database
    private class GetLocationsAsync extends AsyncTask<Void,Void, Void> {
        protected Void doInBackground(Void... d) {
            try {
                LocationClient client = SPWebApiRepository.getInstance().getLocationClient();
                List<LocationModel> locations = client.getLocations();
                countries = new ArrayList<>(locations);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        // Init country spinner after data retrieval
        @Override
        protected void onPostExecute(Void v) {
            if(countries != null && !countries.isEmpty()) {
                initCountries();
            }
            else {
                Toast.makeText(RegisterActivity.this, "Failed to load countries!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Updates user profile information to the database
    private class UpdateUserInfoAsync extends AsyncTask<Void,Void, Void> {
        @Override
        protected Void doInBackground(Void... d) {
            try {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",
                        Locale.getDefault());
                String dateCreated = sdf.format(calendar.getTime());

                String email = txtEmail.getText().toString();
                String phone = txtPhone.getText().toString();
                LocationModel loc = (LocationModel)
                        locationSpinner.getSelectedItem();

                CountrySelectAdapter adapter = (CountrySelectAdapter) locationSpinner.getAdapter();
                LocationModel selectedLocation = adapter.getItem(locationSpinner.getSelectedItemPosition());
                if(selectedLocation != null) {
                    UserInfoModel newUserInfo = new UserInfoModel(user.getUserId(),
                            selectedLocation.getLocationId(), email, phone, dob, dateCreated);
                    appSettings.setUser(userClient.updateUserInfo(appSettings.getUserId(), newUserInfo));
                    userClient.updateUserProfile(userProfileModel);
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Please select a location.",
                            Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        // Updates UI after user info update
        protected void onPostExecute(Void v) {
            onRegistrationComplete();
        }//end onPostExecute
    }
}
