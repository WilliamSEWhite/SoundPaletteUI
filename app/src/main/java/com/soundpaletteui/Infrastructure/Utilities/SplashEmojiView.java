package com.soundpaletteui.Infrastructure.Utilities;

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

public class SplashEmojiView extends View {

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
            "🎬"  // Clapper Board
    };
    private final Random random = new Random();
    private Paint textPaint;
    private final List<EmojiDrawInfo> emojiDrawList = new ArrayList<>();

    public SplashEmojiView(Context context) {
        super(context);
        init();
    }

    public SplashEmojiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SplashEmojiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(80);
        textPaint.setTextAlign(Paint.Align.CENTER);
        setLayerType(View.LAYER_TYPE_SOFTWARE, textPaint);
//        textPaint.setMaskFilter(new BlurMaskFilter(2, BlurMaskFilter.Blur.NORMAL));
        generateEmojiLayout();
    }

    private void generateEmojiLayout() {
        emojiDrawList.clear();
        int numEmojis = 70;
        int width = getWidth();
        int height = getHeight();
        for (int i = 0; i < numEmojis; i++) {
            float x = random.nextFloat() * width;
            float y = -random.nextFloat() * height; // start off-screen top
            float size = 40 + random.nextFloat() * 60;
            float speed = randomSpeed();
            float oscillationSpeed = 0.02f + random.nextFloat() * 0.01f; // how fast they wiggle
            float rotation = random.nextFloat() * 360;
            float rotationSpeed = -1 + random.nextFloat() * 2; // rotate CW/CCW
            emojiDrawList.add(new EmojiDrawInfo(
                    getRandomEmoji(), x, y, size, speed, randomColor(),
                    oscillationSpeed, random.nextFloat() * 2f * (float)Math.PI, rotation, rotationSpeed
            ));
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        generateEmojiLayout();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int saved = canvas.saveLayer(0, 0, width, height, null);

        for (EmojiDrawInfo emoji : emojiDrawList) {
            emoji.y += emoji.speed;

            // Oscillate left/right
            emoji.phase += emoji.oscillationSpeed;
            float offsetX = (float) Math.sin(emoji.phase) * 30f;

            // Reset emoji to top if off bottom
            if (emoji.y > height + 100) {
                emoji.y = -random.nextInt(200);
                emoji.x = random.nextFloat() * width;
            }

            // Rotate slightly each frame
            emoji.rotation += emoji.rotationSpeed;

            // Set paint color and size
            textPaint.setTextSize(emoji.textSize);
            textPaint.setColor(emoji.color);
            Paint.FontMetrics fm = textPaint.getFontMetrics();
            float baselineShift = (fm.ascent + fm.descent) / 2;

            // Draw emoji with rotation and horizontal offset
            canvas.save();
            canvas.translate(emoji.x + offsetX, emoji.y);
            canvas.rotate(emoji.rotation);
            canvas.drawText(emoji.emoji, 0, -baselineShift, textPaint);
            canvas.restore();
        }

        // Gradient overlay to fade top area
        Paint maskPaint = new Paint();
        maskPaint.setShader(new LinearGradient(0, 0, 0, height,
                0x00FFFFFF, 0xFFFFFFFF, Shader.TileMode.CLAMP));
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawRect(0, 0, width, height, maskPaint);
        canvas.restoreToCount(saved);

        // Request next frame
        invalidate();
    }

    private String getRandomEmoji() {
        return emojiArray[random.nextInt(emojiArray.length)];
    }

    private float randomSpeed() {
        return 8f + random.nextFloat() * 18f;
    }

    private int randomColor() {
        int alpha = random.nextInt(156) + 100;
        return android.graphics.Color.argb(alpha, 0, 0, 0); // black w/ transparency
    }

    private static class EmojiDrawInfo {
        String emoji;
        float x, y;
        float textSize;
        float speed;
        int color;
        float oscillationSpeed;
        float phase;
        float rotation;
        float rotationSpeed;

        EmojiDrawInfo(String emoji, float x, float y, float textSize, float speed, int color,
                      float oscillationSpeed, float phase, float rotation, float rotationSpeed) {
            this.emoji = emoji;
            this.x = x;
            this.y = y;
            this.textSize = textSize;
            this.speed = speed;
            this.color = color;
            this.oscillationSpeed = oscillationSpeed;
            this.phase = phase;
            this.rotation = rotation;
            this.rotationSpeed = rotationSpeed;
        }
    }
}
