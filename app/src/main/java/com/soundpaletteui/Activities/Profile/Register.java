package com.soundpaletteui.Activities.Profile;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.soundpaletteui.Infrastructure.Adapters.CountrySelectAdapter;
import com.soundpaletteui.Infrastructure.ApiClients.LocationClient;
import com.soundpaletteui.Infrastructure.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.LocationModel;
import com.soundpaletteui.Infrastructure.Models.UserInfoModel;
import com.soundpaletteui.Infrastructure.Models.UserModel;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Register extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText txtUsername;           // username
    private EditText txtEmail;              // email address
    private EditText txtPhone;              // phone number (optional)
    private EditText txtDob;                // date of birth
    private ImageButton btnCalendar;        // date of birth button

    private Uri imageUri;                   // image URI from external source
    private ImageView profileImage;         // object to display the image
    private Spinner location;               // country
    private ArrayList<LocationModel> countries;             // list of countries

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
        txtEmail = findViewById(R.id.registerEmail);
        txtPhone = findViewById(R.id.registerPhone);
        txtDob = findViewById(R.id.registerDob);
        btnCalendar = findViewById(R.id.btnDatePicker);
        profileImage = findViewById(R.id.registerProfilePicture);
        location = findViewById(R.id.registerLocation);
        Button saveBtn = findViewById(R.id.btnSave);
        saveBtn.setOnClickListener(v -> saveUserProfile());

        intent = getIntent();
        userId = intent.getIntExtra("userId", 0);
        userClient = SPWebApiRepository.getInstance().getUserClient();
        getCountries();
        getUser();
    }

    /** initialize Spinner with list of countries */
    private void getCountries() {
        new GetLocationsAsync().execute();
    }

    private void initCountries(){
        CountrySelectAdapter adapter = new CountrySelectAdapter(this,
                android.R.layout.simple_spinner_item,
                countries);
        location = (Spinner) findViewById(R.id.registerLocation);
        location.setAdapter(adapter); // Set the custom adapter to the spinner
        // You can create an anonymous listener to handle the event when is selected an spinner item
        location.setSelection(0);                              //retain previously selected value
    }

    private void saveUserProfile(){
        new UpdateUserInfoAsync().execute();
    }

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

    private void getUser() {
        new GetUserAsync().execute();
    }

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
        }//end doInBackground

        protected void onPostExecute(Void v) {
            initCountries();
        }//end onPostExecute
    }
    private class UpdateUserInfoAsync extends AsyncTask<Void,Void, Void> {
        protected Void doInBackground(Void... d) {
            try {
                txtEmail = findViewById(R.id.registerEmail);
                txtPhone = findViewById(R.id.registerPhone);

                String email = txtEmail.getText().toString();
                String phone = txtPhone.getText().toString();

                CountrySelectAdapter adapter = (CountrySelectAdapter) location.getAdapter();
                LocationModel selectedLocation = adapter.getItem(location.getSelectedItemPosition());


                UserInfoModel newUserInfo = new UserInfoModel(0, user.getId(), selectedLocation.LocationId, email, phone, new Date(), new Date());
                UserInfoModel userInfo = userClient.updateUserInfo(newUserInfo);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }//end doInBackground

        protected void onPostExecute(Void v) {
            //populateView();
        }//end onPostExecute
    }

}
