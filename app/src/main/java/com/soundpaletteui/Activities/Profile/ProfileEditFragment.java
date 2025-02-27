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
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileEditFragment extends Fragment {

    private RecyclerView recyclerView;
    private MainContentAdapter mainContentAdapter;
    private List<UserModel> userList;
    private String userId;
    private UserModel user;
    private UserInfoModel userInfo;
    private UserClient userClient;
    private View rootView;
    private EditText profile_user;
    private EditText profile_email;
    private EditText profile_phone;
    private EditText profile_bio;
    Button btnCancel;
    Button btnSave;
    private String userName;
    private Spinner location;                   // country
    private ArrayList<LocationModel> countries; // list of countries

    public ProfileEditFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        initComponents();

        profile_user = rootView.findViewById(R.id.profile_user);
        profile_user.setText(user.getUsername());

        profile_email = rootView.findViewById(R.id.profile_email);
        profile_email.setText(user.getUserInfo().getEmail());

        profile_phone = rootView.findViewById(R.id.profile_phone);
        profile_phone.setText(user.getUserInfo().getPhone());

        profile_bio = rootView.findViewById(R.id.profile_bio);
        //profileBio.setText(user.getUserProfile().getBio());

        btnSave = rootView.findViewById(R.id.btnSave);
        btnCancel = rootView.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> cancelProfileEdit());

        getCountries();

        return rootView;
    }

    /** initializes components in the fragment */
    private void initComponents() {
        // Get arguments instead of Intent
        user = AppSettings.getInstance().getUser();

        userClient = SPWebApiRepository.getInstance().getUserClient();
        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);
    }

    /** cancel editing user profile and return to profile fragment */
    private void cancelProfileEdit() {
        ProfileFragment profileFragment = new ProfileFragment();

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.mainScreenFrame, profileFragment);
        transaction.commit();
    }


    /** initialize Spinner with list of countries */
    private void getCountries() {
        new ProfileEditFragment.GetLocationsAsync().execute();
    }

    /** pulls countries from the database for the spinner */
    private void initCountries(){
        CountrySelectAdapter adapter = new CountrySelectAdapter(requireContext(),
                android.R.layout.simple_spinner_item,
                countries);
        location = (Spinner) rootView.findViewById(R.id.profile_location);
        location.setAdapter(adapter);        // Set the custom adapter to the spinner
                                             // You can create an anonymous listener to handle the event when is selected an spinner item
        location.setSelection(0);            //retain previously selected value
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
                Toast.makeText(requireContext(), "Failed to load countries!",
                        Toast.LENGTH_SHORT).show();
            }
        }//end onPostExecute
    }
}
