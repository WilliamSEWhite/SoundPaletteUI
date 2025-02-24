package com.soundpaletteui.Activities.Profile;

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
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.soundpaletteui.R;
import com.soundpaletteui.UISettings;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Allows users to register or update their profile information.
 */
public class Register extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText txtUsername;
    private EditText txtPassword;
    private EditText txtEmail;
    private EditText txtPhone;
    private EditText txtDob;
    private ImageButton btnCalendar;
    private ImageView profileImage;
    private Uri imageUri;
    private String currentPhotoPath;
    private Spinner location;
    private ArrayList<LocationModel> countries;
    private Intent intent;
    private UserModel user;
    private UserClient userClient;
    private int userId;
    private String dob, dateCreated;
    private FrameLayout frameClear;
    private GifImageView gifClear;
    private TextView textClear;
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

    /**
     * Initializes UI components, listeners, and loads user data.
     */
    private void initComponents() {
        txtUsername = findViewById(R.id.registerUsername);
        txtPassword = findViewById(R.id.registerPassword);
        txtEmail = findViewById(R.id.registerEmail);
        txtPhone = findViewById(R.id.registerPhone);
        txtDob = findViewById(R.id.registerDob);
        btnCalendar = findViewById(R.id.btnDatePicker);
        btnCalendar.setColorFilter(Color.WHITE);
        profileImage = findViewById(R.id.registerProfilePicture);
        profileImage.setColorFilter(Color.WHITE);
        location = findViewById(R.id.registerLocation);
        frameClear = findViewById(R.id.frame_clear);
        gifClear = findViewById(R.id.gif_clear);
        textClear = findViewById(R.id.clear_text);
        frameSave = findViewById(R.id.frame_save);
        gifSave = findViewById(R.id.gif_save);
        textSave = findViewById(R.id.save_text);
        btnCalendar.setOnClickListener(v -> chooseDate());
        profileImage.setOnClickListener(v -> showImageSourceDialog());
        frameClear.setOnClickListener(v -> {
            try {
                final GifDrawable clearGif = (GifDrawable) gifClear.getDrawable();
                frameClear.getBackground().mutate().setAlpha(255);
                frameSave.getBackground().mutate().setAlpha(77);
                UISettings.applyBrightnessGradientBackground(findViewById(R.id.root_layout), 50f);
                clearGif.start();
                new android.os.Handler().postDelayed(() -> clearGif.stop(), 800);
            } catch (ClassCastException e) {
                e.printStackTrace();
                Toast.makeText(Register.this, "Error with Clear animation", Toast.LENGTH_SHORT).show();
            }
            clearFields();
        });
        frameSave.setOnClickListener(v -> {
            try {
                final GifDrawable saveGif = (GifDrawable) gifSave.getDrawable();
                frameSave.getBackground().mutate().setAlpha(255);
                frameClear.getBackground().mutate().setAlpha(77);
                UISettings.applyBrightnessGradientBackground(findViewById(R.id.root_layout), 60f);
                saveGif.start();
                new android.os.Handler().postDelayed(() -> saveGif.stop(), 800);
            } catch (ClassCastException e) {
                e.printStackTrace();
                Toast.makeText(Register.this, "Error with Save animation", Toast.LENGTH_SHORT).show();
            }
            saveProfile();
        });
        intent = getIntent();
        userId = intent.getIntExtra("userId", 0);
        userClient = SPWebApiRepository.getInstance().getUserClient();
        getCountries();
        getUser();
    }

    /**
     * Displays a dialog to choose Camera or Gallery for profile image.
     */
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
                    } else {
                        pickImageFromGallery();
                    }
                })
                .show();
    }

    /**
     * Launches an intent to pick an image from the gallery.
     */
    private void pickImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);
    }

    /**
     * Launcher for the gallery intent result.
     */
    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    loadImage();
                }
            });

    /**
     * Loads the chosen image into the profile image using Glide.
     */
    private void loadImage() {
        Glide.with(this).load(imageUri).into(profileImage);
    }

    /**
     * Checks if camera permission is granted and starts camera if so.
     */
    private void checkCameraPermissions() throws IOException {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
        } else {
            takePicture();
        }
    }

    /**
     * Launches camera to take a picture.
     */
    private void takePicture() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photo = createImageFile();
            if(photo != null) {
                imageUri = FileProvider.getUriForFile(this,
                        "com.soundpaletteui.fileprovider", photo);
            }
        }
    }

    /**
     * Creates a temporary file for storing the camera image.
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(null);
        File image = File.createTempFile("JPEG_" + timeStamp, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Handles results of permission requests.
     */
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
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Retrieves the list of available countries in the background.
     */
    private void getCountries() {
        new GetLocationsAsync().execute();
    }

    /**
     * Sets the adapter for the country spinner.
     */
    private void initCountries(){
        CountrySelectAdapter adapter = new CountrySelectAdapter(this, android.R.layout.simple_spinner_item, countries);
        location.setAdapter(adapter);
        location.setSelection(0);
    }

    /**
     * Saves user profile data to the backend.
     */
    private void saveProfile() {
        saveUserProfile();
        Toast.makeText(Register.this, "User profile saved: " + userId, Toast.LENGTH_SHORT).show();
    }

    /**
     * Clears the input fields in the registration form.
     */
    private void clearFields() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtDob.setText("");
    }

    /**
     * Opens a DatePickerDialog for date of birth selection.
     */
    private void chooseDate() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                Register.this,
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = String.format("%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                    txtDob.setText(selectedDate);
                    SimpleDateFormat outputSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    SimpleDateFormat inputSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    try {
                        Date date = inputSdf.parse(selectedDate);
                        dob = outputSdf.format(date);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }, year, month, day
        );
        datePickerDialog.show();
    }

    /**
     * Handles the gallery image selection result.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            displayImage(imageUri);
        }
    }

    /**
     * Displays the selected image with a placeholder or error image.
     */
    private void displayImage(Uri imageUri) {
        Glide.with(this)
                .load(imageUri)
                .placeholder(R.drawable.baseline_person_150)
                .error(R.drawable.baseline_person_150)
                .into(profileImage);
    }

    /**
     * Retrieves user data from the backend.
     */
    private void getUser() {
        new GetUserAsync().execute();
    }

    /**
     * Asynchronously gets user data.
     */
    private class GetUserAsync extends AsyncTask<Void, Void, Void> {
        /**
         * Performs the getUser call in the background.
         */
        @Override
        protected Void doInBackground(Void... d) {
            try {
                user = userClient.getUser(userId);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        /**
         * Updates the UI after user data is retrieved.
         */
        @Override
        protected void onPostExecute(Void v) {
            populateView();
        }
    }

    /**
     * Asynchronously loads location data from the backend.
     */
    private class GetLocationsAsync extends AsyncTask<Void, Void, Void> {
        /**
         * Retrieves location data in the background.
         */
        @Override
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

        /**
         * Initializes the country spinner after data retrieval.
         */
        @Override
        protected void onPostExecute(Void v) {
            if(countries != null && !countries.isEmpty()) {
                initCountries();
            } else {
                Toast.makeText(Register.this, "Failed to load countries!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Asynchronously updates user info in the backend.
     */
    private class UpdateUserInfoAsync extends AsyncTask<Void, Void, Void> {
        /**
         * Performs the update operation in the background.
         */
        @Override
        protected Void doInBackground(Void... d) {
            try {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                String dateCreated = sdf.format(calendar.getTime());
                String email = txtEmail.getText().toString();
                String phone = txtPhone.getText().toString();
                CountrySelectAdapter adapter = (CountrySelectAdapter) location.getAdapter();
                LocationModel selectedLocation = adapter.getItem(location.getSelectedItemPosition());
                if(selectedLocation != null) {
                    UserInfoModel newUserInfo = new UserInfoModel(user.getId(), selectedLocation.getLocationId(), email, phone, dob, dateCreated);
                    userClient.updateUserInfo(newUserInfo);
                } else {
                    Toast.makeText(Register.this, "Please select a location.", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        /**
         * Updates the UI after the user info update.
         */
        @Override
        protected void onPostExecute(Void v) {
            populateView();
        }
    }

    /**
     * Saves the user profile by initiating an async update.
     */
    private void saveUserProfile(){
        new UpdateUserInfoAsync().execute();
    }

    /**
     * Fills in text fields after user data is retrieved.
     */
    private void populateView() {
        txtUsername.setText(user.getUsername());
        txtPassword.setText(user.getPassword());
    }
}
