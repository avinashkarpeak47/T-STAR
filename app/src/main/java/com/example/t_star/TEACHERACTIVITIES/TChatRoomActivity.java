package com.example.t_star.TEACHERACTIVITIES;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import com.bumptech.glide.Glide;
import com.example.t_star.CHATDATA.*;
import com.example.t_star.R;
import com.example.t_star.REAL_TIME_DATABASE.GET_FROM_DATABASE.User_Data_Fetcher;
import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses.ChatHelper;
import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.ModelClasses.Updated_User_Model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.*;
public class TChatRoomActivity extends AppCompatActivity {

    RecyclerView rv;
    EditText et;
    ImageView send , ivProfilePic;
    TextView tvUsername, tvEmailid;
    LinearLayout layoutEmpty;

    List<MessageModel> list = new ArrayList<>();
    MessageAdapter adapter;

    String classId, studentUid, myUid;
    DatabaseReference msgRef;
    String profileImageUrl;

    boolean isChatOpen = false;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_tchat_room);

        classId = getIntent().getStringExtra("classId");
        studentUid = getIntent().getStringExtra("studentUid");
        myUid = FirebaseAuth.getInstance().getUid();


        rv = findViewById(R.id.rvMessages);
        et = findViewById(R.id.etMessage);
        send = findViewById(R.id.btnSend);
        tvUsername = findViewById(R.id.tvUserName);
        tvEmailid = findViewById(R.id.tvEmailID);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        ivProfilePic = findViewById(R.id.imgProfile);



        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(this, list, myUid);
        rv.setAdapter(adapter);

        msgRef = FirebaseDatabase.getInstance()
                .getReference("Chats")
                .child(classId)
                .child(studentUid)
                .child("messages");

        loadStudentInfo();
        listenMessages();

        send.setOnClickListener(v -> sendMessage());
    }

    @Override
    protected void onResume() {
        super.onResume();
        isChatOpen = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isChatOpen = false;
        ChatHelper.resetUnreadCount(classId, studentUid);

    }

    private void loadStudentInfo() {
        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(studentUid)
                .get()
                .addOnSuccessListener(u -> {
                    tvUsername.setText(
                            u.child("firstname").getValue(String.class) + " " +
                                    u.child("lastname").getValue(String.class)
                    );
                    tvEmailid.setText(u.child("email").getValue(String.class));
                });

        // Load Student Profile photo

        User_Data_Fetcher.getUserData(studentUid, new User_Data_Fetcher.UserDataCallback() {
            @Override
            public void onSuccess(Updated_User_Model user) {

                profileImageUrl = user.profileImageUrl;   // 🔥 ADD THIS

                if (user.profileImageUrl != null && !user.profileImageUrl.isEmpty()) {
                    Glide.with(TChatRoomActivity.this)
                            .load(user.profileImageUrl)
                            .centerCrop()
                            .circleCrop()
                            .into(ivProfilePic);
                }
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(TChatRoomActivity.this,
                        "Failed to load profile: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendMessage() {
        String text = et.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        String id = msgRef.push().getKey();
        MessageModel m = new MessageModel(myUid, text);

        msgRef.child(id).setValue(m);
        msgRef.child(id).child("timestamp").setValue(ServerValue.TIMESTAMP);

        ChatHelper.updateLastMessage(classId, studentUid, text, myUid);
        et.setText("");
    }

    private void listenMessages() {
        msgRef.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap) {
                list.clear();

                if (!snap.exists()) {
                    layoutEmpty.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.GONE);
                    return;
                }

                layoutEmpty.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);

                for (DataSnapshot d : snap.getChildren()) {
                    MessageModel m = d.getValue(MessageModel.class);
                    if (m == null) continue;

                    list.add(m);

                    // 🔥 ONLY incoming (student → teacher)
                    if (!m.getSenderId().equals(myUid)) {
                        if (!m.isDelivered()) {
                            ChatHelper.markDelivered(classId, studentUid, d.getKey());
                        }
                        if (isChatOpen && !m.isSeen()) {
                            ChatHelper.markSeen(classId, studentUid, d.getKey());
                        }
                    }
                }

                adapter.notifyDataSetChanged();
                rv.scrollToPosition(list.size() - 1);
            }

            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }



}
