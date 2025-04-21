package com.soundpaletteui.Infrastructure.Utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.soundpaletteui.SPApiServices.ApiClients.FileClient;
import com.soundpaletteui.Infrastructure.Models.FileModel;
import com.soundpaletteui.R;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageUtils {

    /** upload profile image */
    public static void uploadProfileImage(Uri imageUri, int userId, FileClient fileClient, Context context) {
        if(imageUri != null) {
            // upload to API server
            new Thread(() -> {
                Log.d("uploadProfileImage", "uploadProfileImage - userId: " + userId);
                //fileClient.uploadImage(requireContext(), imageUri, user.getUserId());
                File file = FileUtils.uri2File(context, imageUri, 4, userId);
                FileModel fileModel = new FileModel(userId, file.getName(), 4, "https://my.fake.file");

                fileClient.uploadImage(file, userId, fileModel.getFileTypeId(), fileModel.getFileUrl()).enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if (response.isSuccessful()) {
                            Log.d("uploadProfileImage", "Upload successful. File Id: " + response.body());
                        } else {
                            Log.e("uploadProfileImage", "Server error: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        Log.e("uploadProfileImage", "Upload failed", t);
                    }
                });
            }).start();
        }
    }

    /** get profile image by username */
    public static void getProfileImageByUsername(String username, ImageView imageView, Context context) {
        UserUtils.getUserId(username, new UserUtils.UserIdCallback() {
            @Override
            public void onUserIdReceived(int userId) {
                ImageUtils.getProfileImage(
                        userId,
                        SPWebApiRepository.getInstance().getFileClient().getProfileImage(userId),
                        imageView,
                        context
                );
            }

            @Override
            public void onError(Throwable t) {
                Log.e("getProfileImageByUsername", "Could not load profile image", t);
            }
        });
    }

    /** get profile image */
    public static void getProfileImage(int userId, Call<FileModel> fileCall, ImageView imageView, Context context) {
        fileCall.enqueue(new Callback<FileModel>() {
            @Override
            public void onResponse(Call<FileModel> call, Response<FileModel> response) {
                if(response.isSuccessful() && response.body() != null) {
                    String imageUrl = response.body().getFileUrl();
                    String base64Image = response.body().getByteArrayContent();
                    byte[] decodedImage = Base64.decode(base64Image, Base64.DEFAULT);
                    Bitmap image = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
                    if(image != null) {
                        Log.d("getProfileImage", "API call was successful: " + imageUrl);
                        Glide.with(context)
                                .load(image)
                                .placeholder(R.drawable.baseline_person_100)
                                .error(R.drawable.baseline_person_100)
                                .into(imageView);
                    }
                    else {
                        Log.e("getProfileImage", "Bitmap decoding failed");
                        Toast.makeText(context, "Image decoding failed", Toast.LENGTH_SHORT).show();
                        Glide.with(context)
                                .load(R.drawable.baseline_person_100)
                                .into(imageView);
                    }
                }
            }

            @Override
            public void onFailure(Call<FileModel> call, Throwable t) {
                Log.e("getProfileImage", "Error loading profile image", t);
                //imageView = ContextCompat.getDrawable(context, R.drawable.baseline_person_100);
            }
        });
    }

    public static void getPostImage(Call<FileModel> fileCall, ImageView imageView, Context context) {
        getPostImage(0, fileCall, imageView, context);
    }

    /** gets post image */
    public static void getPostImage(int postId, Call<FileModel> fileCall, ImageView imageView, Context context) {
        fileCall.enqueue(new Callback<FileModel>() {
            @Override
            public void onResponse(Call<FileModel> call, Response<FileModel> response) {
                if(response.isSuccessful() && response.body() != null) {
                    String imageUrl = response.body().getFileUrl();
                    String base64Image = response.body().getByteArrayContent();
                    //byte[] decodedImage = Base64.getDecoder().decode(base64Image);
                    byte[] decodedImage = Base64.decode(base64Image, Base64.DEFAULT);
                    Bitmap image = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
                    if(image != null) {
                        Log.d("getPostImage", "API call was successful: " + imageUrl);
                        Glide.with(context)
                                .load(image)
                                .placeholder(R.drawable.baseline_broken_image_400)
                                .error(R.drawable.baseline_broken_image_400)
                                .into(imageView);
                    }
                    else {
                        Log.e("getPostImage", "Bitmap decoding failed");
                        Toast.makeText(context, "Image decoding failed", Toast.LENGTH_SHORT).show();
                        Glide.with(context)
                                .load(R.drawable.baseline_broken_image_400)
                                .into(imageView);
                    }
                }
            }

            @Override
            public void onFailure(Call<FileModel> call, Throwable t) {
                Log.e("getPostImage", "Error loading profile image", t);
                Glide.with(context)
                        .load(R.drawable.baseline_broken_image_400)
                        .into(imageView);
            }
        });
    }

    /** opens the gallery picker after checking storage permissions */
    public static void pickImageFromGallery(Context context, ActivityResultLauncher<Intent> pickImageLauncher) {
        // checks Android version then requests permissions accordingly
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestStoragePermission(context);
                return;
            }
        } else {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestStoragePermission(context);
                return;
            }
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    /** requests permissions to the internal storage */
    private static void requestStoragePermission(Context context) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 100);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        } else {
            Log.e("requestStoragePermission", "Context is not an Activity");
        }

    }
}
