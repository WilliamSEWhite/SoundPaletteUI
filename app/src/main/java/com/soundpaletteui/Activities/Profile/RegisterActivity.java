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
import com.soundpaletteui.Infrastructure.Utilities.ImageUtils;
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

public class RegisterActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    AppSettings appSettings = AppSettings.getInstance();
    private EditText txtUsername;           // username
    private EditText txtEmail;              // email address
    private EditText txtPhone;              // phone number (optional)
    private EditText txtDob;                // date of birth
    private ImageButton btnCalendar;        // date of birth button
    private Button btnClear;                // clear user data button
    private Button btnSave;                 // save button
    private Uri imageUri;                   // image URI from external source
    private ImageView profileImage;         // object to display the image
    private String currentPhotoPath;        // current photo path
    private Spinner location;               // country
    private ArrayList<LocationModel> countries;             // list of countries
    private Intent intent;
    private UserModel user;
    private UserClient userClient;
    private int userId;
    //    private Date dob, dateCreated;
    private Date dob, dateCreated;
    private String Dob;
    private GifImageView gifClear;
    private FrameLayout frameSave;
    private GifImageView gifSave;
    private TextView textSave;
    private UserProfileModel userProfileModel;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private FileClient fileClient;
    /**
     * Sets up the layout and calls initialization.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        View rootView = findViewById(R.id.root_layout);
//        UISettings.applyBrightnessGradientBackground(rootView, 50f);
        initComponents();
        // launches the image picker
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        if(profileImage != null) {
                            //profileImage.setImageURI(imageUri);
                            displayImage(imageUri);
                        } else {
                            Log.e("ProfileEditFragment", "imageView is null");
                        }
                    }
                }
        );
    }

    /** initializes components in the UI */
    private void initComponents() {
        // initialise UI objects
        //txtUsername = findViewById(R.id.registerUsername);
        txtEmail = findViewById(R.id.registerEmail);
        txtPhone = findViewById(R.id.registerPhone);
        LinearLayout pickDateBtn = findViewById(R.id.pick_date);
        pickDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dob == null)
                    dob = new Date();
                int year = dob.getYear() + 1900;
                int month = dob.getMonth();
                int day = dob.getDate();

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        RegisterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Date date = new Date(year-1900, month, dayOfMonth);

                                TextView selectedDate = findViewById(R.id.selected_date);
                                dob = date;
                                selectedDate.setText(new SimpleDateFormat("MMMM dd, yyyy").format(dob));

                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });
        profileImage = findViewById(R.id.registerProfilePicture);
        fileClient = SPWebApiRepository.getInstance().getFileClient();
        location = findViewById(R.id.registerLocation);

        btnSave = findViewById(R.id.btnSave);

        frameSave = findViewById(R.id.frame_save);
        gifSave = findViewById(R.id.gif_save);
        textSave = findViewById(R.id.save_text);

        profileImage.setOnClickListener(v -> editProfileImage());
        frameSave.setOnClickListener(v -> {
            try {
                final GifDrawable saveGif = (GifDrawable) gifSave.getDrawable();
                frameSave.getBackground().mutate().setAlpha(255);
//                UISettings.applyBrightnessGradientBackground(findViewById(R.id.root_layout), 60f);
                saveGif.start();
                new android.os.Handler().postDelayed(() -> saveGif.stop(), 800);
            } catch (ClassCastException e) {
                e.printStackTrace();
                Toast.makeText(RegisterActivity.this, "Error with Save animation", Toast.LENGTH_SHORT).show();
            }
            saveUserInfo();
            uploadProfileImage();
        });

        // get data from previous activity
        intent = getIntent();
        userId = intent.getIntExtra("userId", 0);
        userClient = SPWebApiRepository.getInstance().getUserClient();
        getCountries();     // load countries from database
        user = AppSettings.getInstance().getUser();
        userProfileModel = new UserProfileModel(user.getUserId(), "", "/dev/null");
    }

    /** upload profile image */
    private void uploadProfileImage() {
        ImageUtils.uploadProfileImage(imageUri, user.getUserId(), fileClient, this);
    }

    /** edit the profile image */
    private void editProfileImage() {
        ImageUtils.pickImageFromGallery(this, pickImageLauncher);
    }

    /** loads the image */
    private void loadImage() {
        Glide.with(this).load(imageUri).into(profileImage);
    }

    /** initialize Spinner with list of countries */
    private void getCountries() {
        new GetLocationsAsync().execute();
    }

    /** pulls countries from the database for the spinner */
    private void initCountries(){
        CountrySelectAdapter adapter = new CountrySelectAdapter(this,
                android.R.layout.simple_spinner_item,
                countries);
        location = (Spinner) findViewById(R.id.registerLocation);
        location.setAdapter(adapter); // Set the custom adapter to the spinner
        // You can create an anonymous listener to handle the event when is selected an spinner item
        location.setSelection(0);                              //retain previously selected value
    }

    /** save information to database */
    private void saveUserInfo() {
        // write saving code here
        new UpdateUserInfoAsync().execute();
        Toast.makeText(RegisterActivity.this, "User profile saved ", Toast.LENGTH_SHORT).show();
    }

    /** clear text fields in register activity */
    private void onRegistrationComplete() {
        //Intent i = new Intent(RegisterActivity.this, MainScreenActivity.class);
        Intent i = new Intent(RegisterActivity.this, RegisterTagsActivity.class);
        startActivity(i);
        finish();

    }

    /** displays the image in the register screen */
    private void displayImage(Uri imageUri) {
        //Log.d("RegisterActivity", "Image Uri: " + imageUri);
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

    /** gets user data */
    private void getUser() {
        new GetUserAsync().execute();
    }

    /** asynchronous call to the database for the user data */
    private class GetUserAsync extends AsyncTask<Void,Void, Void> {
        protected Void doInBackground(Void... d) {
            try {
                user = userClient.getUser(userId);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }//end doInBackground

        protected void onPostExecute(Void v) {
            //populateView();
        }//end onPostExecute
    }

    /** async call to pull countries from the database */
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
        /** init country spinner after data retrieval */
        @Override
        protected void onPostExecute(Void v) {
            if(countries != null && !countries.isEmpty()) {
                initCountries();
            }
            else {
                Toast.makeText(RegisterActivity.this, "Failed to load countries!",
                        Toast.LENGTH_SHORT).show();
            }
        }//end onPostExecute
    }

    /** updates user profile information to the database */
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

                CountrySelectAdapter adapter = (CountrySelectAdapter) location.getAdapter();
                LocationModel selectedLocation = adapter.getItem(location.getSelectedItemPosition());
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

        /** updates UI after user info update */
        protected void onPostExecute(Void v) {
            onRegistrationComplete();
        }//end onPostExecute
    }
}
