package com.example.t_star.TEACHERACTIVITIES;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.t_star.Notification.FcmSender;
import com.example.t_star.Notification.NotificationModel;
import com.example.t_star.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CreateNotificationActivity extends AppCompatActivity {

    EditText etTitle, etMessage;
    Button btnSend;

    String classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notification);

        etTitle = findViewById(R.id.etTitle);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        classId = getIntent().getStringExtra("classId");

        if (classId == null) {
            Toast.makeText(this, "Class not selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnSend.setOnClickListener(v -> sendNotification());
    }

    private void sendNotification() {

        String title = etTitle.getText().toString().trim();
        String msg   = etMessage.getText().toString().trim();

        if (title.isEmpty() || msg.isEmpty()) return;

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Class_Notifications")
                .child(classId);

        String id = ref.push().getKey();

        HashMap<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("message", msg);
        map.put("classId", classId);
        map.put("timestamp", System.currentTimeMillis());

        ref.child(id).setValue(map)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Notification Sent", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

//    private void sendPushToClass(String classId, String title, String msg) {
//
//        DatabaseReference ref = FirebaseDatabase.getInstance()
//                .getReference("Students");
//
//        ref.orderByChild("classId").equalTo(classId)
//                .get().addOnSuccessListener(snap -> {
//
//                    for (DataSnapshot ds : snap.getChildren()) {
//                        String token = ds.child("fcmToken").getValue(String.class);
//                        if (token != null)
//                            FcmSender.send(token, title, msg, classId);
//                    }
//                });
//    }
}
