package com.example.t_star.TEACHERACTIVITIES;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.CHATDATA.MessageAdapter;
import com.example.t_star.CHATDATA.MessageModel;
import com.example.t_star.R;
import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses.ChatHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class TChatRoomActivity extends AppCompatActivity {

    RecyclerView rv;
    EditText et;
    ImageView send;
    TextView tvUsername, tvEmailid;
    LinearLayout linearLayoutEmpty;

    List<MessageModel> list = new ArrayList<>();
    MessageAdapter adapter;

    String classId, studentUid, myUid;
    DatabaseReference msgRef;

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

        linearLayoutEmpty = findViewById(R.id.layoutEmpty);

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(this, list, myUid);
        rv.setAdapter(adapter);

        msgRef = FirebaseDatabase.getInstance()
                .getReference("Chats")
                .child(classId)
                .child(studentUid)
                .child("messages");

        // 🔥 Load current student info
        loadStudentInfo();

        // 🔥 Teacher opened chat → reset unread (NOT seen)
        ChatHelper.resetUnreadCount(classId, studentUid);

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
    }

    // =====================================================
    // LOAD STUDENT NAME & EMAIL (CHAT HEADER)
    // =====================================================
    private void loadStudentInfo() {
        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(studentUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot s) {
                        String fname = s.child("firstname").getValue(String.class);
                        String lname = s.child("lastname").getValue(String.class);
                        String email = s.child("email").getValue(String.class);

                        tvUsername.setText(
                                (fname != null ? fname : "Student") + " " +
                                        (lname != null ? lname : "")
                        );

                        tvEmailid.setText(email != null ? email : "");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) { }
                });
    }

    // =====================================================
    // SEND MESSAGE (TEACHER → STUDENT)
    // =====================================================
    private void sendMessage() {
        String text = et.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        String id = msgRef.push().getKey();
        if (id == null) return;

        MessageModel m = new MessageModel(myUid, text);
        msgRef.child(id).setValue(m);
        msgRef.child(id).child("timestamp").setValue(ServerValue.TIMESTAMP);

        // 🔥 Update chat meta only
        ChatHelper.updateLastMessage(classId, studentUid, text);

        et.setText("");
    }

    // =====================================================
    // LISTEN MESSAGES (CORRECT WHATSAPP LOGIC)
    // =====================================================
    private void listenMessages() {
        msgRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();
                if (!snapshot.exists() || snapshot.getChildrenCount() == 0) {
                    linearLayoutEmpty.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    return;
                }

                // 🔥 CHAT EXISTS
                linearLayoutEmpty.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);
                for (DataSnapshot d : snapshot.getChildren()) {
                    MessageModel m = d.getValue(MessageModel.class);
                    if (m == null) continue;

                    list.add(m);

                    // 🔥 ONLY messages SENT BY STUDENT
                    if (!m.getSenderId().equals(myUid)) {

                        // 1️⃣ delivered → when message reaches device
                        if (!m.isDelivered()) {
                            d.getRef().child("delivered").setValue(true);
                        }

                        // 2️⃣ seen → ONLY when chat is open
                        if (isChatOpen && !m.isSeen()) {
                            d.getRef().child("seen").setValue(true);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
                rv.scrollToPosition(list.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) { }
        });
    }
}
