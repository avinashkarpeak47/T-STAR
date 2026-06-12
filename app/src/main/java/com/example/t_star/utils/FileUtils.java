package com.example.t_star.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

public class FileUtils {


    public static String getFileName(Context context, Uri uri) {

        if (uri == null) return "unknown_file";

        String result = null;

        // 1. Preferred method: ContentResolver (SAFE)..
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


    public static String getMimeType(String fileName) {

        fileName = fileName.toLowerCase();

        if (fileName.endsWith(".pdf")) return "application/pdf";
        if (fileName.endsWith(".doc")) return "application/msword";
        if (fileName.endsWith(".docx"))
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        if (fileName.endsWith(".ppt")) return "application/vnd.ms-powerpoint";
        if (fileName.endsWith(".pptx"))
            return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        if (fileName.endsWith(".xls")) return "application/vnd.ms-excel";
        if (fileName.endsWith(".xlsx"))
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        if (fileName.endsWith(".txt")) return "text/plain";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"))
            return "image/jpeg";
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".zip")) return "application/zip";


        return"*/*";
    }
}
