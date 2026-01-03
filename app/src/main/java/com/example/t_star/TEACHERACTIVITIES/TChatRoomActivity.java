package com.example.t_star.TEACHERACTIVITIES;

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
import com.example.t_star.R;
import com.example.t_star.REAL_TIME_DATABASE.GET_FROM_DATABASE.User_Data_Fetcher;
import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses.ChatHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.*;
public class TChatRoomActivity extends AppCompatActivity {

    RecyclerView rv;
    EditText et;
    ImageView send, ivProfilePic, imgAttach;
    TextView tvUsername, tvEmailid;
    LinearLayout layoutEmpty;

    List<MessageModel> list = new ArrayList<>();
    MessageAdapter adapter;

    String classId, studentUid, myUid;
    DatabaseReference msgRef;
    LinearLayout layoutImagePreview;
    RecyclerView rvPreviewImages;
    ProgressBar progressUpload;
    ChatImagePreviewAdapter previewAdapter;


    boolean isChatOpen = false;

    private static final int PICK_IMAGES = 101;
    private final List<Uri> selectedImages = new ArrayList<>();


    @SuppressLint("MissingInflatedId")
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
        imgAttach = findViewById(R.id.imgAttach);


        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

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

        layoutImagePreview = findViewById(R.id.layoutImagePreview);
        rvPreviewImages = findViewById(R.id.rvPreviewImages);
        progressUpload = findViewById(R.id.progressUpload);

        rvPreviewImages.setLayoutManager(
                new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        );


        send.setOnClickListener(v -> sendMessage());
        imgAttach.setOnClickListener(v -> openImagePicker());

    }

    @Override
    protected void onResume() {
        super.onResume();
        isChatOpen = true;
        ChatHelper.resetTeacherUnread(classId, studentUid);
    }


    @Override
    protected void onPause() {
        super.onPause();
        isChatOpen = false;
        ChatHelper.resetTeacherUnread(classId, studentUid);
       // ChatHelper.resetUnreadCount(classId, studentUid);
    }

    private void loadStudentInfo() {
        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(studentUid)
                .get()
                .addOnSuccessListener(u -> {

                    if (isFinishing() || isDestroyed()) return;

                    tvUsername.setText(
                            u.child("firstname").getValue(String.class) + " " +
                                    u.child("lastname").getValue(String.class)
                    );

                    tvEmailid.setText(
                            u.child("email").getValue(String.class)
                    );

                    String img = u.child("profileImageUrl").getValue(String.class);

                    if (!TextUtils.isEmpty(img)) {
                        Glide.with(getApplicationContext())
                                .load(img)
                                .placeholder(R.drawable.taklogo)
                                .error(R.drawable.taklogo)
                                .circleCrop()
                                .into(ivProfilePic);
                    } else {
                        ivProfilePic.setImageResource(R.drawable.taklogo);
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
        // student unread +1
        ChatHelper.incrementUnreadForStudent(classId, studentUid);
        et.setText("");
    }

    private void listenMessages() {
        msgRef.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap) {
                list.clear();
                
                // 🔥 Proper empty check
                if (!snap.hasChildren()) {
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

                    // 🔥 SAFE sender check (NO CRASH)
                    if (!myUid.equals(m.getSenderId())) {

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
                    selectedImages.add(data.getClipData().getItemAt(i).getUri());
                }
            } else if (data.getData() != null) {
                selectedImages.add(data.getData());
            }

            /* SHOW PREVIEW */
            previewAdapter = new ChatImagePreviewAdapter(this, selectedImages);
            rvPreviewImages.setAdapter(previewAdapter);
            layoutImagePreview.setVisibility(View.VISIBLE);

            uploadChatImagesAndSend();
        }
    }
    private void uploadChatImagesAndSend() {

        if (selectedImages.isEmpty()) return;

        progressUpload.setVisibility(View.VISIBLE);
        send.setEnabled(false);

        List<String> uploaded = new ArrayList<>();
        int total = selectedImages.size();

        for (Uri uri : selectedImages) {

            User_Data_Fetcher.uploadChatImage(myUid, uri,
                    new User_Data_Fetcher.UploadCallback() {

                        @Override
                        public void onSuccess(String url) {
                            uploaded.add(url);

                            if (uploaded.size() == total) {
                                sendImageMessage(uploaded);
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(
                                    TChatRoomActivity.this,
                                    "Upload failed",
                                    Toast.LENGTH_SHORT
                            ).show();
                            resetUploadUI();
                        }
                    });
        }
    }

    private void resetUploadUI() {
        progressUpload.setVisibility(View.GONE);
        layoutImagePreview.setVisibility(View.GONE);
        selectedImages.clear();
        send.setEnabled(true);
    }

    private void sendImageMessage(List<String> imageUrls) {
        String id = msgRef.push().getKey();
        if (id == null) return;

        MessageModel m = new MessageModel(myUid, imageUrls);
        msgRef.child(id).setValue(m);
        msgRef.child(id).child("timestamp").setValue(ServerValue.TIMESTAMP);

        ChatHelper.updateLastMessage(
                classId, studentUid, "📷 Photo", myUid
        );

        // student unread +1
        ChatHelper.incrementUnreadForStudent(classId, studentUid);
        resetUploadUI();
    }


    /*
    private void uploadChatImagesAndSend() {
        if (selectedImages.isEmpty()) return;

        List<String> uploadedUrls = new ArrayList<>();
        int total = selectedImages.size();

        for (Uri uri : selectedImages) {
            User_Data_Fetcher.uploadChatImage(myUid, uri,
                    new User_Data_Fetcher.UploadCallback() {
                        @Override
                        public void onSuccess(String url) {
                            uploadedUrls.add(url);
                            if (uploadedUrls.size() == total) {
                                sendImageMessage(uploadedUrls);
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(TChatRoomActivity.this,
                                    "Image upload failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void sendImageMessage(List<String> imageUrls) {
        String msgId = msgRef.push().getKey();
        if (msgId == null) return;

        MessageModel m = new MessageModel(myUid, imageUrls);
        msgRef.child(msgId).setValue(m);
        msgRef.child(msgId).child("timestamp").setValue(ServerValue.TIMESTAMP);

        // Chat list preview
        ChatHelper.updateLastMessage(classId, studentUid, "📷 Photo", myUid);

        // Student unread +1
        ChatHelper.incrementUnreadForStudent(classId, studentUid);

        selectedImages.clear();
    }


     */




}
