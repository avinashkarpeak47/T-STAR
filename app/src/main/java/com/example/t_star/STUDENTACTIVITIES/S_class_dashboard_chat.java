package com.example.t_star.STUDENTACTIVITIES;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.example.t_star.CHATDATA.*;
import com.example.t_star.R;
import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses.ChatHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.*;

public class S_class_dashboard_chat extends AppCompatActivity {

    RecyclerView rvMessages;
    EditText etMessage;
    ImageView btnSend;
    TextView tvUsername, tvEmailid;
    LinearLayout linearLayoutEmpty;

    List<MessageModel> list = new ArrayList<>();
    MessageAdapter adapter;

    String classId, studentUid;
    DatabaseReference msgRef;

    boolean isChatOpen = false; // 🔥 important for seen logic

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_s_class_dashboard_chat);

        // ================= DATA =================
        classId = getIntent().getStringExtra("classId");
        studentUid = FirebaseAuth.getInstance().getUid();

        if (classId == null || studentUid == null) {
            finish();
            return;
        }

        // ================= VIEWS =================
        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        tvUsername = findViewById(R.id.tvUserName);
        tvEmailid = findViewById(R.id.tvEmailID);
        linearLayoutEmpty = findViewById(R.id.layoutEmpty);

        // ================= RECYCLER =================
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(this, list, studentUid);
        rvMessages.setAdapter(adapter);

        // ================= FIREBASE =================
        msgRef = FirebaseDatabase.getInstance()
                .getReference("Chats")
                .child(classId)
                .child(studentUid)
                .child("messages");

        // ================= LOAD TEACHER INFO =================
        loadTeacherInfo();

        // ================= LISTEN MESSAGES =================
        listenMessages();

        // 🔥 student opened chat → ONLY reset unread (NO seen here)
        ChatHelper.resetUnreadCount(classId, studentUid);

        btnSend.setOnClickListener(v -> sendMessage());
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
    }

    // =====================================================
    // LOAD TEACHER NAME & EMAIL
    // =====================================================
    private void loadTeacherInfo() {
        FirebaseDatabase.getInstance()
                .getReference("Classes")
                .child(classId)
                .child("teacherId")
                .get()
                .addOnSuccessListener(snapshot -> {
                    String teacherId = snapshot.getValue(String.class);
                    if (teacherId == null) return;

                    FirebaseDatabase.getInstance()
                            .getReference("Users")
                            .child(teacherId)
                            .get()
                            .addOnSuccessListener(u -> {
                                String fname = u.child("firstname").getValue(String.class);
                                String lname = u.child("lastname").getValue(String.class);
                                String email = u.child("email").getValue(String.class);

                                String name = (fname != null ? fname : "") + " " +
                                        (lname != null ? lname : "");

                                tvUsername.setText(!name.trim().isEmpty() ? name : "Teacher");
                                tvEmailid.setText(email != null ? email : "");
                            });
                });
    }

    // =====================================================
    // SEND MESSAGE (STUDENT → TEACHER)
    // =====================================================
    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        String id = msgRef.push().getKey();
        if (id == null) return;

        MessageModel m = new MessageModel(studentUid, text);
        msgRef.child(id).setValue(m);
        msgRef.child(id).child("timestamp").setValue(ServerValue.TIMESTAMP);

        // 🔥 update chat list meta
        ChatHelper.updateLastMessage(classId, studentUid, text);

        // 🔥 increment unread for teacher
        ChatHelper.incrementUnreadCount(classId, studentUid);

        etMessage.setText("");
    }

    // =====================================================
    // LISTEN MESSAGES (WITH EMPTY LAYOUT + TICK LOGIC)
    // =====================================================
    private void listenMessages() {
        msgRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();

                // 🔥 NO CHAT EXISTS
                if (!snapshot.exists() || snapshot.getChildrenCount() == 0) {
                    linearLayoutEmpty.setVisibility(View.VISIBLE);
                    rvMessages.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    return;
                }

                // 🔥 CHAT EXISTS
                linearLayoutEmpty.setVisibility(View.GONE);
                rvMessages.setVisibility(View.VISIBLE);

                for (DataSnapshot d : snapshot.getChildren()) {
                    MessageModel m = d.getValue(MessageModel.class);
                    if (m == null) continue;

                    list.add(m);

                    // 🔥 ONLY messages SENT BY TEACHER
                    if (!m.getSenderId().equals(studentUid)) {

                        // 1️⃣ delivered when reaches device
                        if (!m.isDelivered()) {
                            d.getRef().child("delivered").setValue(true);
                        }

                        // 2️⃣ seen ONLY if chat is OPEN
                        if (isChatOpen && !m.isSeen()) {
                            d.getRef().child("seen").setValue(true);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
                rvMessages.scrollToPosition(list.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) { }
        });
    }
}
