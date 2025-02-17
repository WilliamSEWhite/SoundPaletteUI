package com.soundpaletteui.Activities.Profile;

import static java.util.Calendar.getInstance;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
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

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;

public class Register extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText txtUsername;           // username
    private EditText txtPassword;           // password
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
    private String dob, dateCreated;
    private String Dob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initComponents();
    }

    /** initializes components in the activity */
    private void initComponents() {
        // initialise UI objects
        txtUsername = findViewById(R.id.registerUsername);
        txtPassword = findViewById(R.id.registerPassword);
        txtEmail = findViewById(R.id.registerEmail);
        txtPhone = findViewById(R.id.registerPhone);
        txtDob = findViewById(R.id.registerDob);
        btnCalendar = findViewById(R.id.btnDatePicker);
        profileImage = findViewById(R.id.registerProfilePicture);
        location = findViewById(R.id.registerLocation);
        btnClear = findViewById(R.id.btnClear);
        btnSave = findViewById(R.id.btnSave);

        // clickable listeners
        btnCalendar.setOnClickListener(v -> {
            chooseDate();
        });
        profileImage.setOnClickListener(v -> {
            showImageSourceDialog();
        });
        btnClear.setOnClickListener(v -> {
            clearFields();
        });
        btnSave.setOnClickListener(v -> {
            saveProfile();
        });

        // get data from previous activity
        intent = getIntent();
        userId = intent.getIntExtra("userId", 0);
        userClient = SPWebApiRepository.getInstance().getUserClient();
        getCountries();     // load countries from database
        getUser();          // load user info from database
    }

    /** shows a dialog with options Camera or Gallery */
    private void showImageSourceDialog() {
        String[] options = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select image source")
                .setItems(options, ((dialog, which) -> {
                    if(which == 0) {
                        try {
                            checkCameraPermissions();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else {
                        pickImageFromGallery();
                    }
                }))
                .show();
    }

    /** launch intent to choose image from gallery */
    private void pickImageFromGallery() {
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
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
            File photo = null;
            photo = createImageFile();
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
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
    private void saveUserProfile(){
        new UpdateUserInfoAsync().execute();
    }

    /** save information to database */
    private void saveProfile() {
        // write saving code here
        saveUserProfile();
        Toast.makeText(Register.this, "User profile saved" + userId, Toast.LENGTH_SHORT).show();
    }

    /** clear text fields in register activity */
    private void clearFields() {
        txtEmail.setText("");
        txtPhone.setText("");
        txtDob.setText("");
    }

    /** populate datepicker dialogue */
    private void chooseDate() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                Register.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // set the date for UI
                        String selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        txtDob.setText(selectedDate);
                        // set the date for API call
                        SimpleDateFormat outputSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",
                                Locale.getDefault());
                        SimpleDateFormat inputSdf = new SimpleDateFormat("yyyy-MM-dd",
                                Locale.getDefault());
                        try {
                            Date date = inputSdf.parse(selectedDate);
                            dob = outputSdf.format(date);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, year, month, day
        );
        datePickerDialog.show();
    }
    /*private void chooseDate() {
        final Calendar calendar = getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                Register.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        SimpleDateFormat inputSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        SimpleDateFormat outputSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",
                                Locale.getDefault());
                        String selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        try {
                            Date date = inputSdf.parse(selectedDate);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        String formattedDate = outputSdf.format(selectedDate);
                        try {
                            dob = outputSdf.parse(formattedDate);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        txtDob.setText(formattedDate);

                    }
                }, year, month, day
        );
        datePickerDialog.show();
    }*/

    /** for pulling image src from the database */
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
            populateView();
        }//end onPostExecute
    }

    /** async call to pull countries from the database */
    private class GetLocationsAsync extends AsyncTask<Void,Void, Void> {
        protected Void doInBackground(Void... d) {
            try {
                LocationClient client = SPWebApiRepository.getInstance().getLocationClient();
                List<LocationModel> locations = client.getLocations();
                // check API response
                /*if (locations == null) {
                    Log.e("GetLocationsAsync", "Error: locations returned null");
                } else {
                    Log.d("GetLocationsAsync", "Successfully retrieved locations: " + locations.size());
                }*/
                countries = new ArrayList<>(locations);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }//end doInBackground

        protected void onPostExecute(Void v) {
            //initCountries();
            if(countries != null && !countries.isEmpty()) {
                initCountries();
            }
            else {
                Toast.makeText(Register.this, "Failed to load countries!",
                        Toast.LENGTH_SHORT).show();
            }
        }//end onPostExecute
    }

    /** updates user profile information to the database */
    private class UpdateUserInfoAsync extends AsyncTask<Void,Void, Void> {
        protected Void doInBackground(Void... d) {
            System.out.println("UpdateUserInfoAsync");
            try {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",
                        Locale.getDefault());
                String dateCreated = sdf.format(calendar.getTime());

                txtEmail = findViewById(R.id.registerEmail);
                txtPhone = findViewById(R.id.registerPhone);

                String email = txtEmail.getText().toString();
                String phone = txtPhone.getText().toString();

                CountrySelectAdapter adapter = (CountrySelectAdapter) location.getAdapter();
                LocationModel selectedLocation = adapter.getItem(location.getSelectedItemPosition());
                if(selectedLocation != null) {
                    UserInfoModel newUserInfo = new UserInfoModel(user.getId(),
                            selectedLocation.getLocationId(), email, phone, dob, dateCreated);
                    UserInfoModel userInfo = userClient.updateUserInfo(newUserInfo);
                    System.out.println("--------------");
                    System.out.println("location Id: " + selectedLocation.getLocationId());
                    System.out.println("user Id: " + user.getId());
                    System.out.println("email: " + email);
                    System.out.println("phone: " + phone);
                    System.out.println("dob: " + dob);
                    System.out.println("dateCreated: " + dateCreated);
                    System.out.println("--------------");
                }
                else {
                    Toast.makeText(Register.this, "Please select a location.",
                            Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }//end doInBackground

        protected void onPostExecute(Void v) {
            System.out.println("Username: " + user.getUsername());
            System.out.println("Password: " + user.getPassword());
            populateView();
        }//end onPostExecute
    }

    /** populates fields with user data
     * username
     * password
     * */
    private void populateView() {
        txtUsername.setText(user.getUsername());
        txtPassword.setText(user.getPassword());
    }

}
