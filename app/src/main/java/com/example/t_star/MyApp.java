package com.example.t_star;

import android.app.Application;
import com.cloudinary.android.MediaManager;
import java.util.HashMap;
import java.util.Map;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", "dr2qe01vj");
        config.put("api_key", "114975574521965");
        config.put("api_secret", "vUNQuK00sdTiNTZ01qnZe6vTq1c");
        MediaManager.init(this, config);
    }

}
