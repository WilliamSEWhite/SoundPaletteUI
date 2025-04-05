package com.soundpaletteui.Infrastructure.Utilities;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.soundpaletteui.R;

/** Utility class for navigation calls within the UI */
public class Navigation {

    /** replaces fragment with navigated fragment */
    public static void replaceFragment(FragmentManager fragmentManager, Fragment fragment, String tag, int containerId) {
        System.out.println("Fragment Tag: " + tag);
        if(tag == null) {
            System.out.println("Fragment Tag was null");
            throw new IllegalArgumentException("Fragment tag cannot be null");
        }
        Fragment currentFragment = fragmentManager.findFragmentById(containerId);
        // don't add the same fragment
        if(currentFragment != null && currentFragment.getTag() != null &&
                currentFragment.getTag().equals(tag)) {
            return;
        }
        // check the back stack for current fragment
        boolean isInBackStack = false;
        for(int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            if(fragmentManager.getBackStackEntryAt(i).getName().equals(tag)) {
                isInBackStack = true;
                break;
            }
        }
        if(isInBackStack) {
            fragmentManager.popBackStack(tag, 0);
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerId, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    /** replaces fragment with navigated fragment */
    public static void replaceFragment(FragmentManager fragmentManager, FragmentTransaction fragmentTransaction, Fragment fragment, String tag, int containerId) {

        System.out.println("Fragment Tag: " + tag);
        if(tag == null) {
            System.out.println("Fragment Tag was null");
            throw new IllegalArgumentException("Fragment tag cannot be null");
        }
        Fragment currentFragment = fragmentManager.findFragmentById(containerId);
        // don't add the same fragment
        if(currentFragment != null && currentFragment.getTag() != null &&
                currentFragment.getTag().equals(tag)) {
            return;
        }
        // check the back stack for current fragment
        boolean isInBackStack = false;
        for(int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            if(fragmentManager.getBackStackEntryAt(i).getName().equals(tag)) {
                isInBackStack = true;
                break;
            }
        }
        if(isInBackStack) {
            fragmentManager.popBackStack(tag, 0);
        }
        fragmentTransaction.replace(containerId, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }
}
