package com.soundpaletteui.Infrastructure.Utilities;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Provides helper methods for applying various animated gradient backgrounds.
 */
public class UISettings {

    private static final int ANIMATION_DURATION = 8000;
    private static final float MIN_BRIGHTNESS = 0.6f;
    private static final float MAX_BRIGHTNESS = 1.0f;
    private static final float LIGHT_SATURATION = 0.2f;

    /**
     * Applies an animated gradient background that changes brightness over time.
     */
    public static void applyBrightnessGradientBackground(View rootView, float baseHue, boolean isDarkMode) {
        if (rootView == null) {
            return;
        }
        final int alpha = 200;
        int startingColor;
        if (!isDarkMode) {
            startingColor = Color.HSVToColor(alpha, new float[]{baseHue, LIGHT_SATURATION, MAX_BRIGHTNESS});
        } else {
            startingColor = Color.argb(alpha, 0, 0, 0);
        }

        int initialBottomColor = Color.HSVToColor(alpha, new float[]{baseHue, 1f, MAX_BRIGHTNESS});
        final GradientDrawable animatedGradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{startingColor, initialBottomColor}
        );
        animatedGradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        rootView.setBackground(animatedGradientDrawable);

        ValueAnimator brightnessAnimator = ValueAnimator.ofFloat(MAX_BRIGHTNESS, MIN_BRIGHTNESS);
        brightnessAnimator.setDuration(ANIMATION_DURATION);
        brightnessAnimator.setRepeatCount(ValueAnimator.INFINITE);
        brightnessAnimator.setRepeatMode(ValueAnimator.REVERSE);
        brightnessAnimator.setInterpolator(new LinearInterpolator());
        brightnessAnimator.addUpdateListener(animation -> {
            float brightness = (float) animation.getAnimatedValue();
            int animatedColor = Color.HSVToColor(alpha, new float[]{baseHue, 1f, brightness});
            animatedGradientDrawable.setColors(new int[]{startingColor, animatedColor});
            rootView.setBackground(animatedGradientDrawable);
        });
        brightnessAnimator.start();
    }

    /**
     * Applies an animated flipped gradient from the bottom to the top.
     */
    public static void applyFlippedBrightnessGradientBackground(View rootView, float baseHue, boolean isDarkMode) {
        if (rootView == null) {
            return;
        }
        final int alpha = 200;
        int startingColor;
        if (!isDarkMode) {
            startingColor = Color.HSVToColor(alpha, new float[]{baseHue, LIGHT_SATURATION, MAX_BRIGHTNESS});
        } else {
            startingColor = Color.argb(alpha, 0, 0, 0);
        }

        int initialBottomColor = Color.HSVToColor(alpha, new float[]{baseHue, 1f, MAX_BRIGHTNESS});
        final GradientDrawable animatedGradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP,
                new int[]{startingColor, initialBottomColor}
        );
        animatedGradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        rootView.setBackground(animatedGradientDrawable);
//
//        ValueAnimator brightnessAnimator = ValueAnimator.ofFloat(MAX_BRIGHTNESS, MIN_BRIGHTNESS);
//        brightnessAnimator.setDuration(ANIMATION_DURATION);
//        brightnessAnimator.setRepeatCount(ValueAnimator.INFINITE);
//        brightnessAnimator.setRepeatMode(ValueAnimator.REVERSE);
//        brightnessAnimator.setInterpolator(new LinearInterpolator());
//        brightnessAnimator.addUpdateListener(animation -> {
//            float brightness = (float) animation.getAnimatedValue();
//            int animatedColor = Color.HSVToColor(alpha, new float[]{baseHue, 1f, brightness});
//            animatedGradientDrawable.setColors(new int[]{startingColor, animatedColor});
//            rootView.setBackground(animatedGradientDrawable);
//        });
//        brightnessAnimator.start();
    }

    /**
     * Applies a gradient with white on top and a changing hue at the bottom.
     */
    public static void applyWhiteTopHueGradientBackground(View rootView, float baseHue) {
        if (rootView == null) {
            return;
        }
        final int alpha = 200;
        int whiteColor = Color.HSVToColor(alpha, new float[]{0f, 0f, 1f});
        int initialBottomColor = Color.HSVToColor(alpha, new float[]{baseHue, 1f, MAX_BRIGHTNESS});
        final GradientDrawable animatedGradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{whiteColor, initialBottomColor}
        );
        animatedGradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        rootView.setBackground(animatedGradientDrawable);

        ValueAnimator hueAnimator = ValueAnimator.ofFloat(0f, 360f);
        hueAnimator.setDuration(30000);
        hueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        hueAnimator.setInterpolator(new LinearInterpolator());
        hueAnimator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            float animatedHue = (baseHue + animatedValue) % 360;
            int animatedBottomColor = Color.HSVToColor(alpha, new float[]{animatedHue, 1f, MAX_BRIGHTNESS});
            animatedGradientDrawable.setColors(new int[]{whiteColor, animatedBottomColor});
            rootView.setBackground(animatedGradientDrawable);
        });
        hueAnimator.start();
    }

    /**
     * Applies a gradient with a light version of the hue at the top and full saturation at the bottom.
     */
    public static void applyHueGradientBackground(View rootView, float baseHue) {
        if (rootView == null) {
            return;
        }
        final int alpha = 200;
        int startingLightColor = Color.HSVToColor(alpha, new float[]{baseHue, LIGHT_SATURATION, MAX_BRIGHTNESS});
        int initialBottomColor = Color.HSVToColor(alpha, new float[]{baseHue, 1f, MAX_BRIGHTNESS});
        final GradientDrawable animatedGradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{startingLightColor, initialBottomColor}
        );
        animatedGradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        rootView.setBackground(animatedGradientDrawable);

        ValueAnimator hueAnimator = ValueAnimator.ofFloat(0f, 360f);
        hueAnimator.setDuration(ANIMATION_DURATION);
        hueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        hueAnimator.setInterpolator(new LinearInterpolator());
        hueAnimator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            float animatedHue = (baseHue + animatedValue) % 360;
            int animatedLightColor = Color.HSVToColor(alpha, new float[]{animatedHue, LIGHT_SATURATION, MAX_BRIGHTNESS});
            int animatedBottomColor = Color.HSVToColor(alpha, new float[]{animatedHue, 1f, MAX_BRIGHTNESS});
            animatedGradientDrawable.setColors(new int[]{animatedLightColor, animatedBottomColor});
            rootView.setBackground(animatedGradientDrawable);
        });
        hueAnimator.start();
    }
}
