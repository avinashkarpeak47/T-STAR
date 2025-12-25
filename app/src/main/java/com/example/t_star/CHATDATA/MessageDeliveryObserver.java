package com.example.t_star.CHATDATA;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.t_star.MyApp;
import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses.ChatHelper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
public class MessageDeliveryObserver {

    public static void start(String classId, String myUid) {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Chats")
                .child(classId)
                .child(myUid)
                .child("messages");

        ref.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot d, String prev) {
                MessageModel m = d.getValue(MessageModel.class);
                if (m == null) return;

                // ✅ INCOMING + APP FOREGROUND = DELIVERED
                if (!m.getSenderId().equals(myUid)
                        && MyApp.isAppInForeground
                        && !m.isDelivered()) {

                    ChatHelper.markDelivered(classId, myUid, d.getKey());
                }
            }

            public void onChildChanged(@NonNull DataSnapshot s, String p) {}
            public void onChildRemoved(@NonNull DataSnapshot s) {}
            public void onChildMoved(@NonNull DataSnapshot s, String p) {}
            public void onCancelled(@NonNull DatabaseError e) {}
        });
    }
}

