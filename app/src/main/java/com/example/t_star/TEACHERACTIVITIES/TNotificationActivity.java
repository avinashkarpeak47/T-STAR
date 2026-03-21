package com.example.t_star.TEACHERACTIVITIES;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.Notification.NotificationModel;
import com.example.t_star.R;
import com.example.t_star.Notification.NotificationAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class TNotificationActivity extends AppCompatActivity {

    RecyclerView rvTeacherNotifications;
    ArrayList<NotificationModel> list = new ArrayList<>();
    NotificationAdapter adapter;
    ImageButton btnCreate;

    String classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tnotification);

        // 👇 RECEIVE classId from THomeActivity
        classId = getIntent().getStringExtra("classId");

        if (classId == null) {
            Toast.makeText(this, "Class not selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvTeacherNotifications = findViewById(R.id.rvTeacherNotifications);
        btnCreate = findViewById(R.id.btnCreate);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvTeacherNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(list);
        rvTeacherNotifications.setAdapter(adapter);

        // 👇 PASS SAME classId to CreateNotificationActivity
        btnCreate.setOnClickListener(v -> {
            Intent i = new Intent(this, CreateNotificationActivity.class);
            i.putExtra("classId", classId);
            startActivity(i);
        });

        loadTeacherNotifications();
    }

    private void loadTeacherNotifications() {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Class_Notifications")
                .child(classId);   // ✔ ONLY THIS CLASS

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    NotificationModel m = ds.getValue(NotificationModel.class);
                    list.add(m);
                }

                Collections.reverse(list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
