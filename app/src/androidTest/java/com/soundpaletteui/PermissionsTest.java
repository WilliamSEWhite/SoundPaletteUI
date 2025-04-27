package com.soundpaletteui;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ActivityInfo;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class PermissionsTest {

    private final Context context = ApplicationProvider.getApplicationContext();

    @Test
    public void checkAppHasDeclaredPermissions() throws Exception {
        String[] expectedPermissions = new String[] {
                Manifest.permission.INTERNET,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.POST_NOTIFICATIONS
        };

        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                context.getPackageName(),
                PackageManager.GET_PERMISSIONS
        );

        String[] declaredPermissions = packageInfo.requestedPermissions;

        for (String permission : expectedPermissions) {
            boolean found = false;
            if (declaredPermissions != null) {
                for (String declared : declaredPermissions) {
                    if (permission.equals(declared)) {
                        found = true;
                        break;
                    }
                }
            }
            assertTrue("Permission " + permission + " is not declared in AndroidManifest.xml", found);
        }
    }

    @Test
    public void checkActivitiesExportedProperly() throws Exception {
        PackageManager pm = context.getPackageManager();

        ActivityInfo splashActivityInfo = pm.getActivityInfo(
                new ComponentName(context, "com.soundpaletteui.Activities.SplashActivity"),
                0
        );
        assertTrue("SplashActivity should be exported", splashActivityInfo.exported);

        ActivityInfo loginActivityInfo = pm.getActivityInfo(
                new ComponentName(context, "com.soundpaletteui.Activities.LoginRegister.LoginActivity"),
                0
        );
        assertTrue("LoginActivity should be exported", loginActivityInfo.exported);

        ActivityInfo mainScreenActivityInfo = pm.getActivityInfo(
                new ComponentName(context, "com.soundpaletteui.Activities.MainScreenActivity"),
                0
        );
        assertEquals("MainScreenActivity should NOT be exported", false, mainScreenActivityInfo.exported);
    }
}
