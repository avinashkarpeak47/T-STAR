package com.example.t_star.utils;

import com.cloudinary.Cloudinary;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryConfig {

    private static Cloudinary cloudinary;

    public static Cloudinary getInstance() {
        if (cloudinary == null) {

            Map<String, Object> config = new HashMap<>();
            config.put("cloud_name", "dr2qe01vj");
            config.put("api_key", "114975574521965");
            config.put("api_secret", "vUNQuK00sdTiNTZ01qnZe6vTq1c");

            config.put("secure", true); // VERY IMPORTANT FOR ANDROID



            cloudinary = new Cloudinary(config);
        }
        return cloudinary;

    }
}