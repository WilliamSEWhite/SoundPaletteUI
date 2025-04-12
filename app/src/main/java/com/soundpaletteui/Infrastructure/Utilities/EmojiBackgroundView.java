package com.soundpaletteui.Views;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EmojiBackgroundView extends View {

    public static final int PATTERN_GRID = 0;
    public static final int PATTERN_SPIRAL = 1;
    public static final int PATTERN_RADIAL = 2;

    private int patternType = PATTERN_GRID;

    private final String[] emojiArray = {
            "🎹", // Musical Keyboard
            "🎼", // Musical Score
            "🎺", // Trumpet
            "🎻", // Violin
            "🎵", // Musical Note
            "🎷", // Saxophone
            "🎸", // Guitar
            "🎤", // Microphone
            "🎧", // Headphone
            "🎭", // Performing Arts
            "🎨", // Artist Palette
            "📻", // Radio
            "🎬"  // Clapper Board
    };

    private final Random random = new Random();
    private Paint textPaint;

    // Pre-calculated emoji data
    private final List<EmojiDrawInfo> emojiDrawList = new ArrayList<>();

    public EmojiBackgroundView(Context context) {
        super(context);
        init();
    }

    public EmojiBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EmojiBackgroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(80);  // Adjust to match your layout
        textPaint.setTextAlign(Paint.Align.CENTER);
        setLayerType(View.LAYER_TYPE_SOFTWARE, textPaint);
        textPaint.setMaskFilter(new BlurMaskFilter(2, BlurMaskFilter.Blur.NORMAL));
    }

    public void setPatternType(int pattern) {
        this.patternType = pattern;
        generateEmojiLayout(); // Refresh only when pattern changes
        invalidate(); // Redraw view
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        generateEmojiLayout(); // Regenerate on size changes
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void generateEmojiLayout() {
        emojiDrawList.clear();
        switch (patternType) {
            case PATTERN_SPIRAL:
                generateSpiralPattern();
                break;
            case PATTERN_RADIAL:
                generateRadialPattern();
                break;
            default:
                generateGridPattern();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Save the layer so that we can mask it with a gradient overlay later
        int width = getWidth();
        int height = getHeight();
        int saved = canvas.saveLayer(0, 0, width, height, null);

        // Draw the emojis as before.
        for (EmojiDrawInfo emoji : emojiDrawList) {
            textPaint.setTextSize(emoji.textSize);
            Paint.FontMetrics fm = textPaint.getFontMetrics();
            float baselineShift = (fm.ascent + fm.descent) / 2;
            canvas.drawText(emoji.emoji, emoji.x, emoji.y - baselineShift, textPaint);
        }

        // Create a gradient that makes the top gradually transparent.
        // Here, top of the view will be almost transparent (alpha = 0) and bottom fully opaque.
        Paint maskPaint = new Paint();
        maskPaint.setShader(new LinearGradient(0, 0, 0, height,
                0x00FFFFFF, // Transparent white at the top
                0xFFFFFFFF, // Opaque white at the bottom
                Shader.TileMode.CLAMP));
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        // Draw the gradient overlay over the entire view.
        canvas.drawRect(0, 0, width, height, maskPaint);
        canvas.restoreToCount(saved);
    }

    private void generateGridPattern() {
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float textHeight = fm.descent - fm.ascent;
        float emojiWidth = textPaint.measureText("🎹");
        int padding = 32;
        // Use a fixed size for grid mode
        float size = 75;

        for (float y = 0; y < getHeight(); y += textHeight + padding) {
            for (float x = 0; x < getWidth(); x += emojiWidth + padding) {
                emojiDrawList.add(new EmojiDrawInfo(getRandomEmoji(), x, y, size));
            }
        }
    }

    private void generateSpiralPattern() {
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        double angle = 0;
        double radius = 0;
        int steps = 160;

        float minSize = 8f;  // smallest emoji size at the center
        float maxSize = 130f;  // largest emoji size at outer edge

        for (int i = 0; i < steps; i++) {
            float x = (float) (centerX + radius * Math.cos(angle) +2);
            float y = (float) (centerY + radius * Math.sin(angle) +10);

            // Calculate size so that emojis grow from center outward.
            float sizeRatio = (float) i / steps;
            float emojiSize = minSize + (maxSize - minSize) * sizeRatio;

            emojiDrawList.add(new EmojiDrawInfo(getRandomEmoji(), x, y, emojiSize));

            angle += 0.3;
            radius += 6;
        }
    }

    private void generateRadialPattern() {
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        int rings = 8;              // number of concentric rings
        int emojisPerRing = 10;      // base count; increases outward
        float startRadius = 70f;     // inner ring radius
        float ringSpacing = 140f;    // distance between rings

        for (int ring = 0; ring < rings; ring++) {
            float radius = startRadius + ring * ringSpacing;
            int count = emojisPerRing + ring * 9; // more emojis on outer rings
            float size = 35f + ring * 5; // increasing emoji size with radius

            for (int i = 0; i < count; i++) {
                double angle = (2 * Math.PI / count) * i;
                float x = (float) (centerX + radius * Math.cos(angle));
                float y = (float) (centerY + radius * Math.sin(angle));
                emojiDrawList.add(new EmojiDrawInfo(getRandomEmoji(), x, y, size));
            }
        }
    }

    private String getRandomEmoji() {
        int index = random.nextInt(emojiArray.length);
        return emojiArray[index];
    }

    private static class EmojiDrawInfo {
        String emoji;
        float x, y;
        float textSize;

        EmojiDrawInfo(String emoji, float x, float y, float textSize) {
            this.emoji = emoji;
            this.x = x;
            this.y = y;
            this.textSize = textSize;
        }
    }
}
