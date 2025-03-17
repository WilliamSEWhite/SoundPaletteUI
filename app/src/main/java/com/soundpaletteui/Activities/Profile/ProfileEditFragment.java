package com.soundpaletteui.Activities.Profile;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import com.soundpaletteui.Infrastructure.Adapters.TagRowAdapter;
import com.soundpaletteui.Infrastructure.Adapters.TagSelectAdapter;
import com.soundpaletteui.Infrastructure.Adapters.UserTagAdapter;
import com.soundpaletteui.Infrastructure.ApiClients.LocationClient;
import com.soundpaletteui.Infrastructure.ApiClients.TagClient;
import com.soundpaletteui.Infrastructure.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.LocationModel;
import com.soundpaletteui.Infrastructure.Models.TagModel;
import com.soundpaletteui.Infrastructure.Models.UserInfoModel;
import com.soundpaletteui.Infrastructure.Models.UserModel;
import com.soundpaletteui.Infrastructure.Models.UserProfileModel;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
import com.soundpaletteui.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ProfileEditFragment extends Fragment {

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
    private TagClient tagClient;
    private RecyclerView recyclerView;
    private UserTagAdapter adapter;
    private List<TagModel> tagList;
    private Button btnAddTags;
    private int fragId;     // from which fragment did I come from

    public ProfileEditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        if(getArguments() != null) {
            fragId = getArguments().getInt("nav", 0);
        }
        try {
            initComponents();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return rootView;
    }

    /** refreshes profile data when resuming fragment */
    @Override
    public void onResume() {
        super.onResume();
        getUser();
        //requireActivity().runOnUiThread(() -> getTags());
        refreshTagList();
    }

    /** initializes components in the fragment */
    private void initComponents() throws IOException {
        // Get arguments instead of Intent
        user = AppSettings.getInstance().getUser();
        userInfo = user.getUserInfo();
        userClient = SPWebApiRepository.getInstance().getUserClient();

        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);

        profile_user = rootView.findViewById(R.id.profile_user);
        profile_email = rootView.findViewById(R.id.profile_email);
        profile_phone = rootView.findViewById(R.id.profile_phone);
        profile_bio = rootView.findViewById(R.id.profile_bio);

        tagClient = SPWebApiRepository.getInstance().getTagClient();
        tagList = new ArrayList<>();

        recyclerView = rootView.findViewById(R.id.userProfileTags);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        btnAddTags = rootView.findViewById(R.id.btnAddTags);
        btnSave = rootView.findViewById(R.id.btnSave);
        btnCancel = rootView.findViewById(R.id.btnCancel);

        btnAddTags.setOnClickListener(v -> editUserTags(new ProfileEditTagsFragment(), "PROFILE_EDIT_TAGS_FRAGMENT"));
        btnCancel.setOnClickListener(v -> returnToProfile());
        btnSave.setOnClickListener(v -> saveProfileEdit());

        getUser();
        getTags();
    }

    /** refreshes the user tag list in the recycler view */
    private void refreshTagList() {
        if (getArguments() != null && getArguments().containsKey("selectedTags")) {
            ArrayList<TagModel> selectedTags = getArguments().getParcelableArrayList("selectedTags");

            if (selectedTags != null && !selectedTags.isEmpty()) {
                // Update the RecyclerView with the new tag list
                adapter = new UserTagAdapter(selectedTags, getContext());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            else {
                Log.d("ProfileEditFragment", "No tags received or empty list");
            }
        }
    }

    /** edit user tags */
    private void editUserTags(Fragment newFragment, String tag) {
        Bundle bundle = new Bundle();
        bundle.putInt("nav", 1);
        newFragment.setArguments(bundle);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        Navigation.replaceFragment(fragmentManager, newFragment, tag, R.id.mainScreenFrame);
    }

    /** retrieves the list of tags from the user profile */
    private void getTags() {
        new Thread(() -> {
            try {
                //System.out.println("tags userId: " + user.getUserId());
                List<TagModel> tags = tagClient.getUserTags(user.getUserId());
                //List<TagModel> tags = tagClient.getTags();
                requireActivity().runOnUiThread(() -> {
                    tagList = tags;
                    adapter = new UserTagAdapter((ArrayList<TagModel>) tagList, requireActivity());
                    recyclerView.setAdapter(adapter);
                });
            } catch (IOException e) {
                requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity(), "Error fetching tags", Toast.LENGTH_SHORT).show());
                throw new RuntimeException(e);
            }
        }).start();
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
            //System.out.println("bio: " + bio);

            CountrySelectAdapter adapter = (CountrySelectAdapter) location.getAdapter();
            LocationModel selectedLocation = adapter.getItem(location.getSelectedItemPosition());

            if(selectedLocation != null) {
                UserInfoModel userInfoModel = new UserInfoModel(user.getUserId(), selectedLocation.getLocationId(), email, phone, userInfo.getDob());
                UserProfileModel userProfileModel = new UserProfileModel(user.getUserId(), bio, picLocation);
                try {
                    UserInfoModel updateUserInfo = userClient.updateUserInfo(user.getUserId(), userInfoModel).getUserInfo();
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
            //System.out.println("returnToProfile()");
            returnToProfile();
        }//end onPostExecute
    }

    /** cancel editing user profile and return to profile fragment */
    private void returnToProfile() {
        if(!isAdded()) {
            return;     // avoid crash if fragment has already detatched
        }
        ProfileFragment profileFragment = new ProfileFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        Navigation.replaceFragment(fragmentManager, profileFragment, "PROFILE_FRAGMENT", R.id.mainScreenFrame);
    }

    /** pulls the user from the database */
    private void getUser() {
        new Thread(() -> {
            try {
                userInfo = userClient.getUserInfo(user.getUserId());
                userProfile = userClient.getUserProfile(user.getUserId());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            profile_location = userInfo.getLocationId();
            /*System.out.println("User Id: " + user.getUserId());
            System.out.println("User Name: " + user.getUsername());   // delete later
            System.out.println("User Email: " + userInfo.getEmail());   // delete later
            System.out.println("UserInfo Location: " + profile_location);   // delete later*/

            getCountries();
            requireActivity().runOnUiThread(this::populateView);
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
        //System.out.println("Location: " + profile_location);
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
        if(userProfile != null) {
            profile_bio.setText(userProfile.getBio());
        }
        else {
            profile_bio.setHint("Please fill in your bio...");
        }
        mainContentAdapter.notifyDataSetChanged();
    }
}
