package com.soundpaletteui.Infrastructure.Utilities;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Navigation {

    private static long lastNavigationTime = 0;
    private static final long NAVIGATION_COOLDOWN_MS = 600;

    private static boolean isNavigationAllowed() {
        long now = System.currentTimeMillis();
        if (now - lastNavigationTime < NAVIGATION_COOLDOWN_MS) {
            return false;
        }
        lastNavigationTime = now;
        return true;
    }

    /** replaces fragment with navigated fragment */
    public static void replaceFragment(FragmentManager fragmentManager, Fragment fragment, String tag, int containerId) {
        if (!isNavigationAllowed()) return;

        System.out.println("Fragment Tag: " + tag);
        if(tag == null) {
            System.out.println("Fragment Tag was null");
            throw new IllegalArgumentException("Fragment tag cannot be null");
        }
        Fragment currentFragment = fragmentManager.findFragmentById(containerId);
        if(currentFragment != null && tag.equals(currentFragment.getTag())) {
            return;
        }
        boolean isInBackStack = false;
        for(int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            if(tag.equals(fragmentManager.getBackStackEntryAt(i).getName())) {
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

    public static void replaceFragment(FragmentManager fragmentManager, FragmentTransaction fragmentTransaction, Fragment fragment, String tag, int containerId) {
        if (!isNavigationAllowed()) return;

        System.out.println("Fragment Tag: " + tag);
        if(tag == null) {
            System.out.println("Fragment Tag was null");
            throw new IllegalArgumentException("Fragment tag cannot be null");
        }
        Fragment currentFragment = fragmentManager.findFragmentById(containerId);
        if(currentFragment != null && tag.equals(currentFragment.getTag())) {
            return;
        }
        boolean isInBackStack = false;
        for(int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            if(tag.equals(fragmentManager.getBackStackEntryAt(i).getName())) {
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
