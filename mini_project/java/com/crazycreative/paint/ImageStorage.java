package com.crazycreative.paint;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class ImageStorage {

    private ImageStorage() {
    }

    @Nullable
    public static String saveBitmapToGallery(Context context, Bitmap bitmap, Bitmap.CompressFormat format, String extension) {
        String fileName = "creative_paint_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + "." + extension;
        String mimeType = "jpg".equals(extension) ? "image/jpeg" : "image/" + extension;

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/CreativePaint");
            values.put(MediaStore.Images.Media.IS_PENDING, 1);
        }

        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri == null) {
            return null;
        }

        try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri)) {
            if (outputStream == null) {
                return null;
            }

            int quality = format == Bitmap.CompressFormat.JPEG ? 92 : 100;
            bitmap.compress(format, quality, outputStream);
        } catch (Exception exception) {
            return null;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues pendingUpdate = new ContentValues();
            pendingUpdate.put(MediaStore.Images.Media.IS_PENDING, 0);
            context.getContentResolver().update(uri, pendingUpdate, null, null);
        }

        return fileName;
    }
}
