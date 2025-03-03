package com.soundpaletteui.Activities.Profile;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.soundpaletteui.Infrastructure.Adapters.CountrySelectAdapter;
import com.soundpaletteui.Infrastructure.Adapters.MainContentAdapter;
import com.soundpaletteui.Infrastructure.ApiClients.LocationClient;
import com.soundpaletteui.Infrastructure.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.LocationModel;
import com.soundpaletteui.Infrastructure.Models.UserInfoModel;
import com.soundpaletteui.Infrastructure.Models.UserModel;
import com.soundpaletteui.Infrastructure.Models.UserProfileModel;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ProfileEditFragment extends Fragment {

    private RecyclerView recyclerView;
    private MainContentAdapter mainContentAdapter;
    private List<UserModel> userList;
    private String userId;
    private UserModel user;
    private UserInfoModel userInfo;
    private UserProfileModel userProfile;
    private UserClient userClient;
    private View rootView;
    private EditText profile_user;
    private EditText profile_email;
    private EditText profile_phone;
    private EditText profile_bio;
    private int profile_location;
    Button btnCancel;
    Button btnSave;
    private String userName;
    private Spinner location;               // country
    private ArrayList<LocationModel> countries;             // list of countries

    public ProfileEditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        initComponents();
        return rootView;
    }
    public static ProfileEditFragment newInstance(int userId) {
        ProfileEditFragment fragment = new ProfileEditFragment();
        Bundle args = new Bundle();
        args.putInt("USER_ID", userId); // Add userId to the Bundle
        fragment.setArguments(args);
        return fragment;
    }


    /** initializes components in the fragment */
    private void initComponents() {
        // Get arguments instead of Intent
        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", 0);
        }
        userClient = SPWebApiRepository.getInstance().getUserClient();
        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);

        profile_user = rootView.findViewById(R.id.profile_user);
        profile_email = rootView.findViewById(R.id.profile_email);
        profile_phone = rootView.findViewById(R.id.profile_phone);
        profile_bio = rootView.findViewById(R.id.profile_bio);
        
        btnSave = rootView.findViewById(R.id.btnSave);
        btnCancel = rootView.findViewById(R.id.btnCancel);
        
        btnCancel.setOnClickListener(v -> returnToProfile());
        btnSave.setOnClickListener(v -> saveProfileEdit());

        getUser();
        //getCountries();
    }

    /** saves the user profile to the database and returns to profile fragment */
    private void saveProfileEdit() {
        saveUserProfile();
        returnToProfile();
    }

    /** saves user profile - calls save async method */
    private void saveUserProfile(){
        new ProfileEditFragment.UpdateUserInfoAsync().execute();
    }

    /** updates user profile information to the database */
    private class UpdateUserInfoAsync extends AsyncTask<Void,Void, Void> {
        protected Void doInBackground(Void... d) {
            String picLocation = "/my/profilePic.jpg";
            String email = profile_email.getText().toString();
            String phone = profile_phone.getText().toString();
            String bio = profile_bio.getText().toString();
            System.out.println("bio: " + bio);

            CountrySelectAdapter adapter = (CountrySelectAdapter) location.getAdapter();
            LocationModel selectedLocation = adapter.getItem(location.getSelectedItemPosition());

            if(selectedLocation != null) {
                UserInfoModel userInfoModel = new UserInfoModel(userId, selectedLocation.getLocationId(), email, phone, userInfo.getDob());
                UserProfileModel userProfileModel = new UserProfileModel(userId, bio, picLocation);
                try {
                    UserInfoModel updateUserInfo = userClient.updateUserInfo(userInfoModel);
                    UserProfileModel updateUserProfile = userClient.updateUserProfile(userProfileModel);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
            else {
                Toast.makeText(requireContext(), "Error fetching locations", Toast.LENGTH_SHORT).show();
            }
            return null;
        }//end doInBackground

        protected void onPostExecute(Void v) {
            System.out.println("Username: " + user.getUsername());
            System.out.println("Password: " + user.getPassword());
            //returnToProfile();
        }//end onPostExecute
    }

    /** cancel editing user profile and return to profile fragment */
    private void returnToProfile() {
        ProfileFragment profileFragment = ProfileFragment.newInstance(userId);

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.mainScreenFrame, profileFragment);
        transaction.commit();
    }

    /** pulls the user from the database */
    private void getUser() {
        new Thread(() -> {
            try {
                user = userClient.getUser(userId);
                userInfo = userClient.getUserInfo(userId);
                userProfile = userClient.getUserProfile(userId);
                //profile_location = userInfo.getLocationId();
                System.out.println("User Name: " + user.getUsername());   // delete later
                System.out.println("User Email: " + userInfo.getEmail());   // delete later
                System.out.println("UserInfo Location: " + userInfo.getLocationId());   // delete later
                getCountries();
                requireActivity().runOnUiThread(this::populateView);
            } catch (IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Error fetching user", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    /** initialize Spinner with list of countries */
    private void getCountries() {
        new ProfileEditFragment.GetLocationsAsync().execute();
    }

    /** pulls countries from the database for the spinner */
    private void initCountries(){
        int i = userInfo.getLocationId();
        CountrySelectAdapter adapter = new CountrySelectAdapter(requireContext(),
                android.R.layout.simple_spinner_item,
                countries);
        location = (Spinner) rootView.findViewById(R.id.profile_location);
        location.setAdapter(adapter); // Set the custom adapter to the spinner
        // You can create an anonymous listener to handle the event when is selected an spinner item
        location.setSelection(i-1);                              //retain previously selected value
        System.out.println("Location: " + profile_location);
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
        }//end doInBackground

        protected void onPostExecute(Void v) {
            //initCountries();
            if(countries != null && !countries.isEmpty()) {
                initCountries();
            }
            else {
                Toast.makeText(requireContext(), "Failed to load countries!",
                        Toast.LENGTH_SHORT).show();
            }
        }//end onPostExecute
    }

    private void populateView() {
        userList.clear();
        userList.add(user);
        profile_user.setText(user.getUsername());
        profile_email.setText(userInfo.getEmail());
        profile_phone.setText(userInfo.getPhone());
        if(userProfile.getBio() != null) {
            profile_bio.setText(userProfile.getBio());
        }
        else {
            profile_bio.setHint("Please fill in your bio...");
        }
        /*if(userInfo.getLocationId() != -1) {
            location.setSelection(userInfo.getLocationId());
        }*/
        mainContentAdapter.notifyDataSetChanged();
    }
}
