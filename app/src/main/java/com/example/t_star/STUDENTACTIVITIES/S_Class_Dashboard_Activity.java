package com.example.t_star.STUDENTACTIVITIES;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.SearchView;

import com.example.t_star.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class S_Class_Dashboard_Activity extends AppCompatActivity {

    // Declare all card views
    CardView cardAttendance, cardStudy, cardTranslator, cardNotes, cardQuiz,
            cardChat, cardAssignment, cardNotification;

SearchView searchDashboard;

    private String classId;

    TextView tv_ClassId , tv_ClassName , tv_Subject , tv_TeacherName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_class_dashboard);

        // Receive classId
        classId = getIntent().getStringExtra("classId");

        if (classId == null || classId.isEmpty()) {
            Toast.makeText(this, "Class not found", Toast.LENGTH_SHORT).show();
            finish(); // Safety: dashboard must open with class context
            return;
        }

        // Initialize card views
        cardAttendance = findViewById(R.id.class_dashboard_attendance);
        cardStudy = findViewById(R.id.class_dashboard_studymaterial);
        cardTranslator = findViewById(R.id.class_dashboard_translator);
        cardNotes = findViewById(R.id.class_dashboard_notes);
        cardQuiz = findViewById(R.id.class_dashboard_quiz);
        cardChat = findViewById(R.id.class_dashboard_chat);
        cardAssignment = findViewById(R.id.class_dashboard_assignment);
        cardNotification = findViewById(R.id.class_dashboard_notification);

        tv_ClassId = findViewById(R.id.tvClassID);
        tv_ClassName = findViewById(R.id.tvClassName);
        tv_Subject = findViewById(R.id.tvSubject);
        tv_TeacherName = findViewById(R.id.tvTeacher);

        searchDashboard = findViewById(R.id.searchDashboard);

        // Click Listeners with classId propagation
        cardAttendance.setOnClickListener(v -> openFeature(S_Class_Dashboard_Attendance.class));
        cardStudy.setOnClickListener(v -> openFeature(S_class_dashboard_studymaterial.class));
        cardTranslator.setOnClickListener(v -> openFeature(S_class_dashboard_translator.class));
        cardNotes.setOnClickListener(v -> openFeature(S_class_dashboard_notes.class));
        cardQuiz.setOnClickListener(v -> openFeature(S_class_dashboard_quiz.class));
        cardChat.setOnClickListener(v -> openFeature(S_class_dashboard_chat.class));
        cardAssignment.setOnClickListener(v -> openFeature(S_Class_Dashboard_Assignment.class));
        cardNotification.setOnClickListener(v -> openFeature(S_class_dashboard_notifications.class));

        searchDashboard.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterCards(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCards(newText);
                return true;

            }
        });


        //Back going Arrow
        MaterialToolbar toolbar = findViewById(R.id.topToolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        loadClassHeader();
    }

    private void openFeature(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtra("classId", classId);
        startActivity(intent);
    }

    private void filterCards(String query) {

        query = query.toLowerCase();

        cardAttendance.setVisibility(
                query.isEmpty() || "attendance".contains(query) ? CardView.VISIBLE : CardView.GONE);

        cardStudy.setVisibility(
                query.isEmpty() || "study material".contains(query) ? CardView.VISIBLE : CardView.GONE);

        cardTranslator.setVisibility(
                query.isEmpty() || "translator".contains(query) ? CardView.VISIBLE : CardView.GONE);

        cardNotes.setVisibility(
                query.isEmpty() || "notes".contains(query) ? CardView.VISIBLE : CardView.GONE);

        cardQuiz.setVisibility(
                query.isEmpty() || "quiz".contains(query) ? CardView.VISIBLE : CardView.GONE);

        cardChat.setVisibility(
                query.isEmpty() || "chat".contains(query) ? CardView.VISIBLE : CardView.GONE);

        cardAssignment.setVisibility(
                query.isEmpty() || "assignment".contains(query) ? CardView.VISIBLE : CardView.GONE);

        cardNotification.setVisibility(
                query.isEmpty() || "notification".contains(query) ? CardView.VISIBLE : CardView.GONE);
    }


    private void loadClassHeader() {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Classes")
                .child(classId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) return;

                String className = snapshot.child("className").getValue(String.class);
                String subject = snapshot.child("subject").getValue(String.class);
                String teacherId = snapshot.child("teacherId").getValue(String.class);


                tv_ClassName.setText(className);
                tv_Subject.setText(subject);
                tv_ClassId.setText(classId);

                loadTeacherName(teacherId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadTeacherName(String teacherId) {

        if (teacherId == null) return;

        FirebaseDatabase.getInstance().getReference("Users")
                .child(teacherId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String fname = snapshot.child("firstname").getValue(String.class);
                        String lname = snapshot.child("lastname").getValue(String.class);

                        tv_TeacherName.setText(fname + " " + lname);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }



}