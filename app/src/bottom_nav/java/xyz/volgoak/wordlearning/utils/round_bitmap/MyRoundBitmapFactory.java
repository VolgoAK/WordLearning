package xyz.volgoak.wordlearning.utils.round_bitmap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.BitmapCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;

import java.io.InputStream;

public class MyRoundBitmapFactory {
    private static final String TAG = "RoundedBitmapDrawableFa";

    private static class DefaultRoundedBitmapDrawable extends MyRoundBitmap {
        DefaultRoundedBitmapDrawable(Resources res, Bitmap bitmap) {
            super(res, bitmap);
        }

        @Override
        public void setMipMap(boolean mipMap) {
            if (mBitmap != null) {
                BitmapCompat.setHasMipMap(mBitmap, mipMap);
                invalidateSelf();
            }
        }

        @Override
        public boolean hasMipMap() {
            return mBitmap != null && BitmapCompat.hasMipMap(mBitmap);
        }

        @Override
        void gravityCompatApply(int gravity, int bitmapWidth, int bitmapHeight,
                                Rect bounds, Rect outRect) {
            GravityCompat.apply(gravity, bitmapWidth, bitmapHeight,
                    bounds, outRect, ViewCompat.LAYOUT_DIRECTION_LTR);
        }
    }

    /**
     * Returns a new drawable by creating it from a bitmap, setting initial target density based on
     * the display metrics of the resources.
     */
    @NonNull
    public static MyRoundBitmap create(@NonNull Resources res, @Nullable Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= 21) {
            return new MyRoundBitmap21(res, bitmap);
        }
        return new DefaultRoundedBitmapDrawable(res, bitmap);
    }

    /**
     * Returns a new drawable, creating it by opening a given file path and decoding the bitmap.
     */
    @NonNull
    public static MyRoundBitmap create(@NonNull Resources res, @NonNull String filepath) {
        final MyRoundBitmap drawable = create(res, BitmapFactory.decodeFile(filepath));
        if (drawable.getBitmap() == null) {
            Log.w(TAG, "RoundedBitmapDrawable cannot decode " + filepath);
        }
        return drawable;
    }


    /**
     * Returns a new drawable, creating it by decoding a bitmap from the given input stream.
     */
    @NonNull
    public static MyRoundBitmap create(@NonNull Resources res, @NonNull InputStream is) {
        final MyRoundBitmap drawable = create(res, BitmapFactory.decodeStream(is));
        if (drawable.getBitmap() == null) {
            Log.w(TAG, "RoundedBitmapDrawable cannot decode " + is);
        }
        return drawable;
    }

    private MyRoundBitmapFactory() {}
}
