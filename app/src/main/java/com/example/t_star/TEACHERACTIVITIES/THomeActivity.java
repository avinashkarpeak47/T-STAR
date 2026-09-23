

package com.example.t_star.TEACHERACTIVITIES;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;

import com.example.t_star.R;
import com.example.t_star.TQuizActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import androidx.gridlayout.widget.GridLayout;

import java.util.*;

public class THomeActivity extends AppCompatActivity {

    String classId, className;
    private String myUserId;
    MaterialToolbar toolbar;
    TextView tvClassID, tvClassName, tvSubject, tvTeacher, tvMsgIndicator;

    SearchView searchView;
    GridLayout gridLayout;

    CardView cardCreateAssignment, cardAttendance, cardNotes,
            cardChat, cardStudents, cardResources, cardNotifications,cardTranslator;

    Map<CardView, String> cardMap = new LinkedHashMap<>();
    List<CardView> originalOrder = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thome);
        myUserId = FirebaseAuth.getInstance().getUid();

        classId   = getIntent().getStringExtra("classId");
        className = getIntent().getStringExtra("className");


        if (className == null) className = "Class";

        if (classId == null) {
            Toast.makeText(this, "Class data missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupCardMap();
        setupSearch();
        fetchClassDetails();
        setupClicks();
        listenTeacherUnreadMessages();
    }

    private void initViews() {

        toolbar     = findViewById(R.id.toolbar);
        searchView  = findViewById(R.id.searchViewGrid);
        gridLayout  = findViewById(R.id.gridActions);

        tvClassID   = findViewById(R.id.tvClassID);
        tvClassName = findViewById(R.id.tvClassName);
        tvSubject   = findViewById(R.id.tvSubject);
        tvTeacher   = findViewById(R.id.tvTeacher);
        tvMsgIndicator = findViewById(R.id.tvMsgNo);


        cardCreateAssignment = findViewById(R.id.cardCreateAssignment);
        cardAttendance       = findViewById(R.id.cardAttendance);
        cardNotes            = findViewById(R.id.cardNotes);
        cardChat             = findViewById(R.id.cardChat);
        cardStudents         = findViewById(R.id.cardStudents);
        cardResources        = findViewById(R.id.cardResources);
        cardNotifications    = findViewById(R.id.cardNotifications);
        //cardQuiz             = findViewById(R.id.cardQuiz);
        cardTranslator       = findViewById(R.id.cardTransalator);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupCardMap() {

        cardMap.put(cardCreateAssignment, "assignment");
        cardMap.put(cardAttendance, "attendance");
        cardMap.put(cardNotes, "notes");   // 👈 fixed lowercase
        cardMap.put(cardChat, "chat");
        cardMap.put(cardStudents, "students");
        cardMap.put(cardResources, "resources");
        //cardMap.put(cardQuiz, "quiz");
        cardMap.put(cardNotifications, "notification");
        cardMap.put(cardTranslator, "translator");

        originalOrder.addAll(cardMap.keySet());
    }

    private void fetchClassDetails() {

        FirebaseDatabase.getInstance()
                .getReference("Classes")
                .child(classId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (!snapshot.exists()) return;

                        tvClassID.setText(snapshot.child("classId").getValue(String.class));
                        tvClassName.setText(snapshot.child("className").getValue(String.class));
                        tvSubject.setText(snapshot.child("subject").getValue(String.class));

                        String teacherId = snapshot.child("teacherId").getValue(String.class);

                        if (teacherId == null) {
                            tvTeacher.setText("-");
                            return;
                        }

                        FirebaseDatabase.getInstance()
                                .getReference("Users")
                                .child(teacherId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snap) {

                                        String first = snap.child("firstname").getValue(String.class);
                                        String last  = snap.child("lastname").getValue(String.class);

                                        String name = ((first != null ? first : "") + " " +
                                                (last != null ? last : "")).trim();

                                        tvTeacher.setText(name.isEmpty() ? "-" : name);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        tvTeacher.setText("-");
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(THomeActivity.this,
                                "Failed to load class data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void listenTeacherUnreadMessages() {
        DatabaseReference chatRef =
                FirebaseDatabase.getInstance()
                        .getReference("Chats")
                        .child(classId);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                boolean hasUnread = false;
                int totalUnread = 0;

                for (DataSnapshot studentSnap : snapshot.getChildren()) {

                    Integer uc =
                            studentSnap.child("unread")
                                    .child("teacher")
                                    .getValue(Integer.class);

                    if (uc != null && uc > 0) {
                        hasUnread = true;
                        totalUnread += uc;
                    }
                }

                if (hasUnread) {
                    tvMsgIndicator.setVisibility(View.VISIBLE);

                    // 👉 if you want NUMBER
                    // tvMsgIndicator.setText(String.valueOf(totalUnread));

                } else {
                    tvMsgIndicator.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvMsgIndicator.setVisibility(View.GONE);
            }
        });
    }


    private void setupClicks() {

        open(cardCreateAssignment, TAsssignmentActivity.class);
        open(cardAttendance,      TAttendanceActivity.class);
        open(cardNotes,           TNoteActivity.class);
        open(cardChat,            TChatsActivity.class);
        open(cardStudents,        TStudentsActivity.class);
        open(cardResources,       TResourcesActivity.class);
        open(cardNotifications,   TNotificationActivity.class);
//        open(cardQuiz,            TQuizActivity.class);
        open(cardTranslator,       TTranslatorActivity.class);
    }

    private void open(CardView card, Class<?> cls) {
        card.setOnClickListener(v -> {
            Intent i = new Intent(this, cls);
            i.putExtra("classId", classId);
            i.putExtra("className", className);
            startActivity(i);
        });
    }

    private void setupSearch() {

        searchView.setOnCloseListener(() -> {
            restoreOriginalOrder();
            return false;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String q) { reorderGrid(q); return true; }
            @Override public boolean onQueryTextChange(String t) { reorderGrid(t); return true; }
        });
    }

    private void reorderGrid(String text) {

        if (text == null || text.trim().isEmpty()) {
            restoreOriginalOrder();
            return;
        }

        text = text.toLowerCase();

        List<CardView> matched = new ArrayList<>();
        List<CardView> others = new ArrayList<>();

        for (Map.Entry<CardView,String> e : cardMap.entrySet()) {
            if (e.getValue().contains(text)) matched.add(e.getKey());
            else others.add(e.getKey());
        }

        gridLayout.removeAllViews();
        for (CardView c : matched) gridLayout.addView(c);
        for (CardView c : others)  gridLayout.addView(c);
    }

    private void restoreOriginalOrder() {
        gridLayout.removeAllViews();
        for (CardView c : originalOrder) gridLayout.addView(c);
    }
}
