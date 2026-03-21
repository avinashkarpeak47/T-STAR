package com.example.t_star.Notification;

import org.json.JSONObject;
import okhttp3.*;

public class FcmSender {

    private static final String SERVER_KEY = "YOUR_FCM_SERVER_KEY_HERE";

    public static void send(String token, String title,
                            String message, String classId) {

        try {
            JSONObject json = new JSONObject();

            JSONObject notification = new JSONObject();
            notification.put("title", title);
            notification.put("body", message);

            JSONObject data = new JSONObject();
            data.put("title", title);
            data.put("message", message);
            data.put("classId", classId);

            json.put("notification", notification); // background
            json.put("data", data);                 // foreground
            json.put("to", token);

            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.get("application/json")
            );

            Request request = new Request.Builder()
                    .url("https://fcm.googleapis.com/fcm/send")
                    .header("Authorization", "key=" + SERVER_KEY)
                    .post(body)
                    .build();

            client.newCall(request).execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
