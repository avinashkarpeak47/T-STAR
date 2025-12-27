package com.example.t_star.STUDENTACTIVITIES;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import com.bumptech.glide.Glide;
import com.example.t_star.CHATDATA.*;
import com.example.t_star.MyApp;
import com.example.t_star.R;
import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses.ChatHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.*;

public class S_class_dashboard_chat extends AppCompatActivity {

    RecyclerView rv;
    EditText et;
    ImageView send, ivProfilePic;
    TextView tvName, tvEmail;
    LinearLayout empty;
    List<MessageModel> list = new ArrayList<>();
    MessageAdapter adapter;

    String classId, myUid , profileImageUrl;
    DatabaseReference msgRef;
    boolean isOpen = false;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_s_class_dashboard_chat);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        classId = getIntent().getStringExtra("classId");
        myUid = FirebaseAuth.getInstance().getUid();

        rv = findViewById(R.id.rvMessages);
        et = findViewById(R.id.etMessage);
        send = findViewById(R.id.btnSend);
        tvName = findViewById(R.id.tvUserName);
        tvEmail = findViewById(R.id.tvEmailID);
        empty = findViewById(R.id.layoutEmpty);
        ivProfilePic = findViewById(R.id.imgProfile);


        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(this, list, myUid);
        rv.setAdapter(adapter);

        msgRef = FirebaseDatabase.getInstance()
                .getReference("Chats")
                .child(classId)
                .child(myUid)
                .child("messages");

        loadTeacher();
        listen();

        send.setOnClickListener(v -> send());
    }

    @Override
    protected void onResume() {
        super.onResume();
        isOpen = true;
        ChatHelper.resetStudentUnread(classId, myUid);
    }


    @Override protected void onPause() {
        super.onPause();
        isOpen = false;
        ChatHelper.resetStudentUnread(classId, myUid);

    }

    private void loadTeacher() {
        FirebaseDatabase.getInstance()
                .getReference("Classes")
                .child(classId)
                .child("teacherId")
                .get()
                .addOnSuccessListener(t -> {

                    String teacherId = t.getValue(String.class);
                    if (TextUtils.isEmpty(teacherId)) return;

                    FirebaseDatabase.getInstance()
                            .getReference("Users")
                            .child(teacherId)
                            .get()
                            .addOnSuccessListener(u -> {

                                // ---------- NAME ----------
                                String firstName = u.child("firstname").getValue(String.class);
                                String lastName  = u.child("lastname").getValue(String.class);

                                String fullName =
                                        (firstName != null ? firstName : "") +
                                                " " +
                                                (lastName != null ? lastName : "");

                                tvName.setText(
                                        !fullName.trim().isEmpty()
                                                ? fullName
                                                : "Teacher"
                                );


                                // ---------- EMAIL ----------
                                String email = u.child("email").getValue(String.class);
                                tvEmail.setText(email != null ? email : "");

                                // ---------- PROFILE IMAGE ----------
                                String profileImageUrl =
                                        u.child("profileImageUrl").getValue(String.class);

                                if (!TextUtils.isEmpty(profileImageUrl)) {
                                    Glide.with(S_class_dashboard_chat.this)
                                            .load(profileImageUrl)
                                            .placeholder(R.drawable.taklogo)
                                            .error(R.drawable.taklogo)
                                            .circleCrop()
                                            .into(ivProfilePic);
                                } else {
                                    ivProfilePic.setImageResource(R.drawable.taklogo);
                                }
                            });
                });
    }


    private void send() {
        String text = et.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        String id = msgRef.push().getKey();
        MessageModel m = new MessageModel(myUid, text);
        msgRef.child(id).setValue(m);
        msgRef.child(id).child("timestamp").setValue(ServerValue.TIMESTAMP);

        ChatHelper.updateLastMessage(classId, myUid, text, myUid);
        // teacher unread +1
        ChatHelper.incrementUnreadForTeacher(classId, myUid);
      //  ChatHelper.incrementUnreadCount(classId, myUid);


        et.setText("");
    }

    private void listen() {
        msgRef.addValueEventListener(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot s) {
                list.clear();

                if (!s.exists()) {
                    empty.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.GONE);
                    return;
                }

                empty.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);

                for (DataSnapshot d : s.getChildren()) {
                    MessageModel m = d.getValue(MessageModel.class);
                    list.add(m);

                    if (!m.getSenderId().equals(myUid)) {

                        if (MyApp.isAppInForeground && !m.isDelivered()) {
                            ChatHelper.markDelivered(classId, myUid, d.getKey());
                        }

                        if (isOpen && !m.isSeen()) {
                            ChatHelper.markSeen(classId, myUid, d.getKey());
                        }
                    }

                }
                adapter.notifyDataSetChanged();
                rv.scrollToPosition(list.size() - 1);
            }
            public void onCancelled(@NonNull DatabaseError e) {}
        });
    }
}
