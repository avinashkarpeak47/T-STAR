package com.example.t_star.STUDENTACTIVITIES;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import com.bumptech.glide.Glide;
import com.example.t_star.CHATDATA.*;
import com.example.t_star.MyApp;
import com.example.t_star.R;
import com.example.t_star.REAL_TIME_DATABASE.GET_FROM_DATABASE.User_Data_Fetcher;
import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses.ChatHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.*;

public class S_class_dashboard_chat extends AppCompatActivity {

    RecyclerView rv;
    EditText et;
    ImageView send, ivProfilePic,imgAttach;
    TextView tvName, tvEmail;
    LinearLayout empty;
    List<MessageModel> list = new ArrayList<>();
    MessageAdapter adapter;

    String classId, myUid , profileImageUrl;
    DatabaseReference msgRef;

    LinearLayout layoutImagePreview;
    RecyclerView rvPreviewImages;
    ProgressBar progressUpload;
    ChatImagePreviewAdapter previewAdapter;


    boolean isOpen = false;

    private static final int PICK_IMAGES = 101;
    private final List<Uri> selectedImages = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
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
        imgAttach = findViewById(R.id.imgAttach);

        layoutImagePreview = findViewById(R.id.layoutImagePreview);
        rvPreviewImages = findViewById(R.id.rvPreviewImages);
        progressUpload = findViewById(R.id.progressUpload);

        rvPreviewImages.setLayoutManager(
                new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        );


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
        imgAttach.setOnClickListener(v -> openImagePicker());

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

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_IMAGES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGES && resultCode == RESULT_OK && data != null) {
            selectedImages.clear();

            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    selectedImages.add(uri);
                }
            } else if (data.getData() != null) {
                selectedImages.add(data.getData());
            }

            uploadChatImagesAndSend();
        }
    }
    private void uploadChatImagesAndSend() {

        if (selectedImages.isEmpty()) return;

        List<String> uploadedUrls = new ArrayList<>();
        final int total = selectedImages.size();

        for (Uri uri : selectedImages) {

            User_Data_Fetcher.uploadChatImage(
                    myUid,
                    uri,
                    new User_Data_Fetcher.UploadCallback() {

                        @Override
                        public void onSuccess(String downloadUrl) {
                            uploadedUrls.add(downloadUrl);

                            if (uploadedUrls.size() == total) {
                                sendImageMessage(uploadedUrls);
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(
                                    S_class_dashboard_chat.this,
                                    "Image upload failed",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );
        }
    }
    private void sendImageMessage(List<String> imageUrls) {

        String msgId = msgRef.push().getKey();
        if (msgId == null) return;

        MessageModel m = new MessageModel(myUid, imageUrls);

        msgRef.child(msgId).setValue(m);
        msgRef.child(msgId).child("timestamp").setValue(ServerValue.TIMESTAMP);

        // 🔥 chat list preview
        ChatHelper.updateLastMessage(
                classId,
                myUid,
                "📷 Photo",
                myUid
        );

        // unread increment
        ChatHelper.incrementUnreadForTeacher(classId, myUid);

        selectedImages.clear();
    }




}
