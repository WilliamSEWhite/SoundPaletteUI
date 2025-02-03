package com.soundpaletteui.Activities.Profile;

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
import com.soundpaletteui.Activities.Home.HomeActivity;
import com.soundpaletteui.Infrastructure.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.UserModel;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;

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
    private String[] countries;             // list of countries
    private ArrayAdapter<String> adapter;   // adapter for country list
    private Intent intent;
    private UserModel user;
    private UserClient userClient;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initComponents();
    }

    /** initializes components in the activity */
    private void initComponents() {
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
        initCountries();

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

        intent = getIntent();
        userId = intent.getIntExtra("userId", 0);
        userClient = SPWebApiRepository.getInstance().getUserClient();
        getUser();
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
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

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

    private void takePicture() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photo = null;
            photo = createImageFile();
            if(photo != null) {
                imageUri = FileProvider.getUriForFile(this,
                        "com.soundpaletteui.fileprovider", photo);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                cameraLauncher.launch(takePictureIntent);
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
    private void initCountries() {
        countries = getResources().getStringArray(R.array.countries);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        location.setAdapter(adapter);
    }

    /** save information to database */
    private void saveProfile() {
        // write saving code here
        Toast.makeText(Register.this, "User profile saved", Toast.LENGTH_SHORT).show();
    }

    /** clear text fields in register activity */
    private void clearFields() {
        txtEmail.setText("");
        txtPhone.setText("");
        txtDob.setText("");
    }

    /** choose date and save to the DOB rosfield */
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
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        txtDob.setText(selectedDate);
                    }
                }, year, month, day
        );
        datePickerDialog.show();
    }

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
            System.out.println("Username: " + user.getUsername());
            System.out.println("Password: " + user.getPassword());
            populateView();
        }//end onPostExecute
    }

    /** populates fields with user data */
    private void populateView() {
        txtUsername.setText(user.getUsername());
        txtPassword.setText(user.getPassword());
    }

}
