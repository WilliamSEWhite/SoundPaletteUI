package com.soundpaletteui.Activities.Profile;

import android.Manifest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.soundpaletteui.Infrastructure.Adapters.CountrySelectAdapter;
import com.soundpaletteui.Infrastructure.Adapters.MainContentAdapter;
import com.soundpaletteui.Infrastructure.Adapters.TagRowAdapter;
import com.soundpaletteui.Infrastructure.Adapters.TagBasicAdapter;

import com.soundpaletteui.SPApiServices.ApiClients.FileClient;
import com.soundpaletteui.SPApiServices.ApiClients.LocationClient;
import com.soundpaletteui.SPApiServices.ApiClients.TagClient;
import com.soundpaletteui.SPApiServices.ApiClients.UserClient;

import com.soundpaletteui.SPApiServices.ApiEndpoints.FileApiEndpoints;
import com.soundpaletteui.Infrastructure.Models.FileModel;
import com.soundpaletteui.Infrastructure.Adapters.TagBasicAdapter;
import com.soundpaletteui.SPApiServices.ApiClients.LocationClient;
import com.soundpaletteui.SPApiServices.ApiClients.TagClient;
import com.soundpaletteui.SPApiServices.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.LocationModel;
import com.soundpaletteui.Infrastructure.Models.TagModel;
import com.soundpaletteui.Infrastructure.Models.User.UserInfoModel;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.Infrastructure.Models.User.UserProfileModel;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.Infrastructure.Utilities.FileUtils;
import com.soundpaletteui.Infrastructure.Utilities.ImageUtils;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import com.soundpaletteui.Infrastructure.Utilities.DarkModePreferences;
import com.soundpaletteui.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileEditFragment extends Fragment {

    private MainContentAdapter mainContentAdapter;
    private List<UserModel> userList;
    private int userId;
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
    private Spinner location;
    private ArrayList<LocationModel> countries;
    private TagClient tagClient;
    private RecyclerView recyclerView;
    private TagBasicAdapter adapter;
    private List<TagModel> tagList;
    private Button btnAddTags;
    private ImageButton btnEditImage;
    private int fragId;     // from which fragment did I come from
    private ImageView imageView;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private static final int PICK_IMAGE_REQUEST = 1001;
    private FileClient fileClient;
    private Uri imageUri;
    private Drawable defaultProfileImage;

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
        // Apply dark mode gradient background
        boolean isDarkMode = DarkModePreferences.isDarkModeEnabled(rootView.getContext());
        UISettings.applyBrightnessGradientBackground(rootView, 50f, isDarkMode);
        // launches the image picker
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        if(imageView != null) {
                            imageView.setImageURI(imageUri);
                        } else {
                            Log.e("ProfileEditFragment", "imageView is null");
                        }
                    }
                }
        );

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getUser();
        refreshTagList();
    }

    /** initialize components for the fragment */
    private void initComponents() throws IOException {
        // Get arguments instead of Intent
        user = AppSettings.getInstance().getUser();
        userInfo = user.getUserInfo();
        userClient = SPWebApiRepository.getInstance().getUserClient();
        userId = user.getUserId();

        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);

        fileClient = SPWebApiRepository.getInstance().getFileClient();
        imageView = rootView.findViewById(R.id.profile_picture);
        defaultProfileImage = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_person_100);
        btnEditImage = rootView.findViewById(R.id.edit_profile_button);

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

        btnEditImage.setOnClickListener(v -> editProfileImage());
        btnAddTags.setOnClickListener(v -> editUserTags(new ProfileEditTagsFragment(), "PROFILE_EDIT_TAGS_FRAGMENT"));
        btnCancel.setOnClickListener(v -> returnToProfile());
        btnSave.setOnClickListener(v -> saveProfileEdit());

        loadProfileImage();
        getUser();
        getTags();
    }

    /** loads the profile image **/
    private void loadProfileImage() {
//        Call<FileModel> call = fileClient.getProfileImage(userId);
//        ImageUtils.getProfileImage(userId, call, imageView, requireContext());
        ImageUtils.getProfileImageByUsername(AppSettings.getInstance().getUsername(),
                imageView,
                requireContext());
    }
    /** upload profile image */
    private void uploadProfileImage() {
        ImageUtils.uploadProfileImage(imageUri, userId, fileClient, requireContext());
    }

    /** edit the profile image */
    private void editProfileImage() {
        ImageUtils.pickImageFromGallery(requireContext(), pickImageLauncher);
    }

    /** refreshes the list of user tags */
    private void refreshTagList() {
        if (getArguments() != null && getArguments().containsKey("selectedTags")) {
            ArrayList<TagModel> selectedTags = getArguments().getParcelableArrayList("selectedTags");

            if (selectedTags != null && !selectedTags.isEmpty()) {
                adapter = new TagBasicAdapter(selectedTags, getContext());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            else {
                Log.d("ProfileEditFragment", "No tags received or empty list");
            }
        }
    }

    /** edit the user tags */
    private void editUserTags(Fragment newFragment, String tag) {
        Bundle bundle = new Bundle();
        bundle.putInt("nav", 1);
        newFragment.setArguments(bundle);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        Navigation.replaceFragment(fragmentManager, newFragment, tag, R.id.mainScreenFrame);
    }

    /** get the list of user tags */
    private void getTags() {
        new Thread(() -> {
            try {
                List<TagModel> tags = tagClient.getUserTags(user.getUserId());
                requireActivity().runOnUiThread(() -> {
                    tagList = tags;
                    adapter = new TagBasicAdapter((ArrayList<TagModel>) tagList, requireActivity());
                    recyclerView.setAdapter(adapter);
                });
            } catch (IOException e) {
                requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity(), "Error fetching tags", Toast.LENGTH_SHORT).show());
                throw new RuntimeException(e);
            }
        }).start();
    }

    /** save user profile and return to user profile fragment */
    private void saveProfileEdit() {
        uploadProfileImage();
        saveUserProfile();
        returnToProfile();
    }

    /** saves user profile */
    private void saveUserProfile(){
        new UpdateUserInfoAsync().execute();
    }

    /** updates the user fields in the fragment */
    private class UpdateUserInfoAsync extends AsyncTask<Void,Void, Void> {
        protected Void doInBackground(Void... d) {
            String picLocation;
            if(imageUri == null) {
                picLocation = "/dev/null/";
            }
            else {
                picLocation = imageView.toString();
            }
            String email = profile_email.getText().toString();
            String phone = profile_phone.getText().toString();
            String bio = profile_bio.getText().toString();

            CountrySelectAdapter adapter = (CountrySelectAdapter) location.getAdapter();
            LocationModel selectedLocation = adapter.getItem(location.getSelectedItemPosition());

            if(selectedLocation != null) {
                UserInfoModel userInfoModel = new UserInfoModel(user.getUserId(), selectedLocation.getLocationId(), email, phone, userInfo.getDob());
                UserProfileModel userProfileModel = new UserProfileModel(user.getUserId(), bio, picLocation);
                try {
                    userClient.updateUserInfo(user.getUserId(), userInfoModel);
                    userClient.updateUserProfile(userProfileModel);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                Toast.makeText(requireContext(), "Error fetching locations", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
        protected void onPostExecute(Void v) {
            returnToProfile();
        }
    }

    /** returns to profile fragment */
    private void returnToProfile() {
        if(!isAdded()) {
            return;
        }
        ProfileFragment profileFragment = new ProfileFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        Navigation.replaceFragment(fragmentManager, profileFragment, "PROFILE_FRAGMENT", R.id.mainScreenFrame);
    }

    /** gets the user info */
    private void getUser() {
        new Thread(() -> {
            try {
                userInfo = userClient.getUserInfo(user.getUserId());
                userProfile = userClient.getUserProfile(user.getUserId());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            profile_location = userInfo.getLocationId();
            getCountries();
            requireActivity().runOnUiThread(this::populateView);
        }).start();
    }

    /** gets the list of countries */
    private void getCountries() {
        new GetLocationsAsync().execute();
    }

    /** pull country list from the database */
    private void initCountries(){
        int i = userInfo.getLocationId();
        CountrySelectAdapter adapter = new CountrySelectAdapter(requireContext(),
                android.R.layout.simple_spinner_item,
                countries);
        location = rootView.findViewById(R.id.profile_location);
        location.setAdapter(adapter);
        location.setSelection(i-1);
    }

    /** get locations for the spinner */
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
        protected void onPostExecute(Void v) {
            if(countries != null && !countries.isEmpty()) {
                initCountries();
            }
            else {
                Toast.makeText(requireContext(), "Failed to load countries!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** populates the fields in the fragment */
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
