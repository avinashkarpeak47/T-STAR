package com.example.t_star.STUDENTACTIVITIES;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.Notification.NotificationAdapter;
import com.example.t_star.Notification.NotificationModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class S_class_dashboard_notifications extends AppCompatActivity {

    RecyclerView rvNotifications;
    ArrayList<NotificationModel> list = new ArrayList<>();
    NotificationAdapter adapter;

    String classId;
    boolean isFirstLoad = true;   // 🔥 IMPORTANT

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_class_dashboard_notifications);

        classId = getIntent().getStringExtra("classId");

        if (classId == null) {
            finish();
            return;
        }

        rvNotifications = findViewById(R.id.rvNotifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NotificationAdapter(list);
        rvNotifications.setAdapter(adapter);

        listenForNotifications();
    }

    // 🔔 REAL-TIME LISTENER
    private void listenForNotifications() {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Class_Notifications")
                .child(classId);

        ref.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {

                NotificationModel model = snapshot.getValue(NotificationModel.class);
                if (model == null) return;

                list.add(0, model);
                adapter.notifyItemInserted(0);

                // 🔥 Skip old data first time
                if (!isFirstLoad) {
                    showLocalNotification(
                            model.getTitle(),
                            model.getMessage()
                    );
                }
            }

            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, String s) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, String s) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });

        isFirstLoad = false;
    }

    // 🔔 STATUS BAR POP-UP
    private void showLocalNotification(String title, String message) {

        String CHANNEL_ID = "class_notify";

        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Class Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.s_notification)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
