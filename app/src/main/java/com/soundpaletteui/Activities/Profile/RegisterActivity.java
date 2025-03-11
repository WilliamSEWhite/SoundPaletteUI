package com.soundpaletteui.Activities.Profile;

import static java.util.Calendar.getInstance;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.soundpaletteui.Infrastructure.ApiClients.LocationClient;
import com.soundpaletteui.Infrastructure.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.LocationModel;
import com.soundpaletteui.Infrastructure.Models.UserInfoModel;
import com.soundpaletteui.Infrastructure.Models.UserModel;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
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
    /**
     * Sets up the layout and calls initialization.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        View rootView = findViewById(R.id.root_layout);
        UISettings.applyBrightnessGradientBackground(rootView, 50f);
        initComponents();
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
        profileImage.setColorFilter(Color.WHITE);
        location = findViewById(R.id.registerLocation);
        //btnClear = findViewById(R.id.btnClear);
        btnSave = findViewById(R.id.btnSave);

        frameSave = findViewById(R.id.frame_save);
        gifSave = findViewById(R.id.gif_save);
        textSave = findViewById(R.id.save_text);
        profileImage.setOnClickListener(v -> showImageSourceDialog());
        frameSave.setOnClickListener(v -> {
            try {
                final GifDrawable saveGif = (GifDrawable) gifSave.getDrawable();
                frameSave.getBackground().mutate().setAlpha(255);
                UISettings.applyBrightnessGradientBackground(findViewById(R.id.root_layout), 60f);
                saveGif.start();
                new android.os.Handler().postDelayed(() -> saveGif.stop(), 800);
            } catch (ClassCastException e) {
                e.printStackTrace();
                Toast.makeText(RegisterActivity.this, "Error with Save animation", Toast.LENGTH_SHORT).show();
            }
            saveUserInfo();
        });

        // get data from previous activity
        intent = getIntent();
        userId = intent.getIntExtra("userId", 0);
        userClient = SPWebApiRepository.getInstance().getUserClient();
        getCountries();     // load countries from database
        user = AppSettings.getInstance().getUser();
    }

    /** shows a dialog with options Camera or Gallery */
    private void showImageSourceDialog() {
        String[] options = {"Camera", "Gallery"};
        new AlertDialog.Builder(this)
                .setTitle("Select image source")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        try {
                            checkCameraPermissions();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else {
                        pickImageFromGallery();
                    }
                })
                .show();
    }

    /** launch intent to choose image from gallery */
    private void pickImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);
    }

    /** launch gallery to choose image */
    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    loadImage();
                }
    });

    /** loads the image */
    private void loadImage() {
        Glide.with(this).load(imageUri).into(profileImage);
    }

    /** checks camera permissions */
    private void checkCameraPermissions() throws IOException {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
        }
        else {
            takePicture();
        }
    }

    /** takes a picture with the camera - incomplete */
    private void takePicture() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photo = createImageFile();
            if(photo != null) {
                imageUri = FileProvider.getUriForFile(this,
                        "com.soundpaletteui.fileprovider", photo);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                cameraLauncher.launch(takePictureIntent);
            }
        }
    }

    /** creates the image file */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(null);
        File image = File.createTempFile("JPEG_" + timeStamp, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /** checks camera permissions */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 101 && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                takePicture();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
        }
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

    /** saves user profile - calls save async method */
    /* not needed **
    private void saveUserProfile(){
        new UpdateUserInfoAsync().execute();
    }*/

    /** save information to database */
    private void saveUserInfo() {
        // write saving code here
        new UpdateUserInfoAsync().execute();
        Toast.makeText(RegisterActivity.this, "User profile saved" + userId, Toast.LENGTH_SHORT).show();
    }

    /** clear text fields in register activity */
    private void onRegistrationComplete() {
        //Intent i = new Intent(RegisterActivity.this, MainScreenActivity.class);
        Intent i = new Intent(RegisterActivity.this, RegisterTagsActivity.class);
        startActivity(i);
        finish();

    }

    /** Handles the gallery image selection result */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            displayImage(imageUri);
        }
    }

    /** displays the image in the register screen */
    private void displayImage(Uri imageUri) {
        Glide.with(this)
                .load(imageUri)
                .placeholder(R.drawable.baseline_person_150)    // placeholder image
                .error(R.drawable.baseline_person_150)          // use placeholder image if error
                .into(profileImage);
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
                    System.out.println("--------------");
                    System.out.println("location Id: " + selectedLocation.getLocationId());
                    System.out.println("user Id: " + user.getUserId());
                    System.out.println("email: " + email);
                    System.out.println("phone: " + phone);
                    System.out.println("dob: " + dob);
                    System.out.println("dateCreated: " + dateCreated);
                    System.out.println("--------------");
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
