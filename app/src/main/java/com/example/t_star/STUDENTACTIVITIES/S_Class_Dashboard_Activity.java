package com.example.t_star.STUDENTACTIVITIES;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.t_star.CHATDATA.MessageDeliveryObserver;
import com.example.t_star.R;
import com.example.t_star.TEACHERACTIVITIES.THomeActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class S_Class_Dashboard_Activity extends AppCompatActivity {

    // Declare all card views
    CardView cardAttendance, cardStudy, cardTranslator, cardNotes, cardQuiz,
            cardChat, cardEvents, cardAssignment, cardNotification;

    private String classId;
    private FirebaseUser currentUser;
    private String myUid;

    TextView tv_ClassId , tv_ClassName , tv_Subject , tv_TeacherName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_class_dashboard);


        // user id fetch
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(S_Class_Dashboard_Activity.this, "No authenticated user", Toast.LENGTH_SHORT).show();
            return ;
        }
        myUid = currentUser.getUid();
        MessageDeliveryObserver.start(classId, myUid); //msg delevered tick

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
        cardEvents = findViewById(R.id.class_dashboard_events);
        cardAssignment = findViewById(R.id.class_dashboard_assignment);
        cardNotification = findViewById(R.id.class_dashboard_notification);

        tv_ClassId = findViewById(R.id.tvClassID);
        tv_ClassName = findViewById(R.id.tvClassName);
        tv_Subject = findViewById(R.id.tvSubject);
        tv_TeacherName = findViewById(R.id.tvTeacher);

        // Click Listeners with classId propagation
        cardAttendance.setOnClickListener(v -> openFeature(S_Class_Dashboard_Attendance.class));
        cardStudy.setOnClickListener(v -> openFeature(S_class_dashboard_studymaterial.class));
        cardTranslator.setOnClickListener(v -> openFeature(S_class_dashboard_translator.class));
        cardNotes.setOnClickListener(v -> openFeature(S_class_dashboard_notes.class));
        cardQuiz.setOnClickListener(v -> openFeature(S_class_dashboard_quiz.class));
        cardChat.setOnClickListener(v -> openFeature(S_class_dashboard_chat.class));
        cardEvents.setOnClickListener(v -> openFeature(S_class_dashboard_events.class));
        cardAssignment.setOnClickListener(v -> openFeature(S_Class_Dashboard_Assignment.class));
        cardNotification.setOnClickListener(v -> openFeature(S_class_dashboard_notifications.class));


        //Back going Arrow
        MaterialToolbar toolbar = findViewById(R.id.topToolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        loadClassHeader();
    }
    protected void onStart() {
        super.onStart();
        MessageDeliveryObserver.start(classId, myUid); //msg delevered tick
    }

    private void openFeature(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtra("classId", classId);
        startActivity(intent);
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
                String subject = snapshot.child("subjectName").getValue(String.class);
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