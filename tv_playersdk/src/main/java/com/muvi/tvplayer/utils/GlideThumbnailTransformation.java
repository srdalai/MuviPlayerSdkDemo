package com.muvi.tvplayer.utils;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class GlideThumbnailTransformation extends BitmapTransformation {

    private int ROWS = 7;
    private int COLUMNS = 10;
    private int THUMBNAIL_DURATION = 10000; // milliseconds

    private int x;
    private int y;

    public GlideThumbnailTransformation(long position, int ROWS, int COLUMNS, int THUMBNAIL_DURATION) {
        this.ROWS = ROWS;
        this.COLUMNS = COLUMNS;
        this.THUMBNAIL_DURATION = THUMBNAIL_DURATION;

        int square = (int) position / THUMBNAIL_DURATION;
        y = square / COLUMNS;
        x = square % COLUMNS;
    }


    public GlideThumbnailTransformation(long position) {
        int square = (int) position / THUMBNAIL_DURATION;
        y = square / ROWS;
        x = square % COLUMNS;
    }

    private int getX() {
        return x;
    }

    private int getY() {
        return y;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform,
                               int outWidth, int outHeight) {
        int width = toTransform.getWidth() / COLUMNS;
        int height = toTransform.getHeight() / ROWS;
        return Bitmap.createBitmap(toTransform, x * width, y * height, width, height);
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        byte[] data = ByteBuffer.allocate(8).putInt(x).putInt(y).array();
        messageDigest.update(data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GlideThumbnailTransformation that = (GlideThumbnailTransformation) o;

        if (x != that.x) return false;
        return y == that.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
