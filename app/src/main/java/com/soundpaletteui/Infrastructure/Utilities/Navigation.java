package com.soundpaletteui.Infrastructure.Utilities;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

// Handles navigation and fragment replacement in the app
public class Navigation {

    private static long lastNavigationTime = 0;
    private static final long NAVIGATION_COOLDOWN_MS = 600;

    // Checks if navigation is allowed based on cooldown
    private static boolean isNavigationAllowed() {
        long now = System.currentTimeMillis();
        if (now - lastNavigationTime < NAVIGATION_COOLDOWN_MS) {
            return false;
        }
        lastNavigationTime = now;
        return true;
    }

    // Replaces fragment with navigated fragment
    public static void replaceFragment(FragmentManager fragmentManager, Fragment fragment, String tag, int containerId) {
        if (!isNavigationAllowed()) return;

        if(tag == null) {
            throw new IllegalArgumentException("Fragment tag cannot be null");
        }

        // Check if the current fragment is already displayed
        Fragment currentFragment = fragmentManager.findFragmentById(containerId);
        if(currentFragment != null && tag.equals(currentFragment.getTag())) {
            return;
        }

        // Check if the fragment is already in the back stack
        boolean isInBackStack = false;
        for(int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            if(tag.equals(fragmentManager.getBackStackEntryAt(i).getName())) {
                isInBackStack = true;
                break;
            }
        }

        // If already in back stack, pop it
        if(isInBackStack) {
            fragmentManager.popBackStack(tag, 0);
        }

        // Replace the current fragment and add the transaction to the back stack
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerId, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    // Overloaded version that uses an existing FragmentTransaction
    public static void replaceFragment(FragmentManager fragmentManager, FragmentTransaction fragmentTransaction, Fragment fragment, String tag, int containerId) {
        System.out.println("Fragment Tag: " + tag);
        if(tag == null) {
            throw new IllegalArgumentException("Fragment tag cannot be null");
        }

        // Check if the current fragment is already displayed
        Fragment currentFragment = fragmentManager.findFragmentById(containerId);
        if(currentFragment != null && tag.equals(currentFragment.getTag())) {
            return;
        }

        // Check if the fragment is already in the back stack
        boolean isInBackStack = false;
        for(int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            if(tag.equals(fragmentManager.getBackStackEntryAt(i).getName())) {
                isInBackStack = true;
                break;
            }
        }

        // If already in back stack, pop it
        if(isInBackStack) {
            fragmentManager.popBackStack(tag, 0);
        }

        // If already in back stack, pop it
        fragmentTransaction.replace(containerId, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }
}
