package com.example.t_star;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.cloudinary.android.MediaManager;
import java.util.HashMap;
import java.util.Map;

public class MyApp extends Application implements Application.ActivityLifecycleCallbacks {

    public static boolean isAppInForeground = false;
    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);

        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", "dr2qe01vj");
        config.put("api_key", "114975574521965");
        config.put("api_secret", "vUNQuK00sdTiNTZ01qnZe6vTq1c");
        MediaManager.init(this, config);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (++activityReferences == 1 && !isActivityChangingConfigurations) {
            isAppInForeground = true;   // ✅ APP FOREGROUND
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        isActivityChangingConfigurations = activity.isChangingConfigurations();
        if (--activityReferences == 0 && !isActivityChangingConfigurations) {
            isAppInForeground = false;  // ❌ APP BACKGROUND
        }
    }

    // unused
    public void onActivityCreated(Activity a, Bundle b) {}
    public void onActivityResumed(Activity a) {}
    public void onActivityPaused(Activity a) {}
    public void onActivitySaveInstanceState(Activity a, Bundle b) {}
    public void onActivityDestroyed(Activity a) {}
}

/*
public class MyApp extends Application implements Application.ActivityLifecycleCallbacks {

    public static boolean isAppInForeground = false;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);

        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", "dxgl7lgyb");

        MediaManager.init(this, config);

    }

    @Override public void onActivityResumed(Activity activity) {
        isAppInForeground = true;
    }

    @Override public void onActivityPaused(Activity activity) {
        isAppInForeground = false;
    }

    // unused but required
    @Override
    public void onActivityStarted(@NonNull Activity activity) {}
    @Override
    public void onActivityStopped(@NonNull Activity activity) {}


    public void onActivityCreated(Activity a, Bundle b) {}
    public void onActivitySaveInstanceState(Activity a, Bundle b) {}
    public void onActivityDestroyed(Activity a) {}

}
*/