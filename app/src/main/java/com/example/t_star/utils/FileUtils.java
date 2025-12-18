package com.example.t_star.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

public class FileUtils {

    /**
     * Safely returns display file name from a content URI.
     * Works with ACTION_OPEN_DOCUMENT and scoped storage.
     */
    public static String getFileName(Context context, Uri uri) {

        if (uri == null) return "unknown_file";

        String result = null;

        // 1. Preferred method: ContentResolver (SAFE)
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, null, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            } catch (Exception ignored) {
                // Fallback handled below
            } finally {
                if (cursor != null) cursor.close();
            }
        }

        // 2. Fallback method: Extract from path
        if (result == null || result.trim().isEmpty()) {
            String path = uri.getPath();
            if (path != null) {
                int cut = path.lastIndexOf('/');
                result = (cut != -1) ? path.substring(cut + 1) : path;
            }
        }

        // 3. Final fallback (guaranteed non-null)
        if (result == null || result.trim().isEmpty()) {
            result = "assignment_file";
        }

        return result;

    }
}