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
import com.soundpaletteui.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileEditFragment extends Fragment {

    private RecyclerView recyclerView;
    private MainContentAdapter mainContentAdapter;
    private List<UserModel> userList;
    private int userId;
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
        
        btnCancel.setOnClickListener(v -> cancelProfileEdit());

        getUser();
        getCountries();
        //getUserInfo();
    }

    /** cancel editing user profile and return to profile fragment */
    private void cancelProfileEdit() {
        ProfileFragment profileFragment = ProfileFragment.newInstance(userId);

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.mainScreenFrame, profileFragment);
        transaction.commit();
    }

    private void getUser() {
        new Thread(() -> {
            try {
                user = userClient.getUser(userId);
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
        CountrySelectAdapter adapter = new CountrySelectAdapter(requireContext(),
                android.R.layout.simple_spinner_item,
                countries);
        location = (Spinner) rootView.findViewById(R.id.profile_location);
        location.setAdapter(adapter); // Set the custom adapter to the spinner
        // You can create an anonymous listener to handle the event when is selected an spinner item
        location.setSelection(0);                              //retain previously selected value
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

    private void populateView() {
        userList.clear();
        userList.add(user);
        mainContentAdapter.notifyDataSetChanged();
    }

}
