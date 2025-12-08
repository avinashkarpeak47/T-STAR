package com.example.t_star.STUDENTACTIVITIES;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.t_star.R;

public class S_Class_Dashboard extends AppCompatActivity {

    // Declare all card views
    CardView cardAttendance, cardStudy, cardTranslator, cardNotes, cardQuiz,
            cardChat, cardEvents, cardAssignment, cardNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_class_dashboard);

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

        // Click Listeners
        cardAttendance.setOnClickListener(v ->
                startActivity(new Intent(this, S_class_dashboard_attendance.class)));

        cardStudy.setOnClickListener(v ->
                startActivity(new Intent(this, S_class_dashboard_studymaterial.class)));

        cardTranslator.setOnClickListener(v ->
                startActivity(new Intent(this, S_class_dashboard_translator.class)));

        cardNotes.setOnClickListener(v ->
                startActivity(new Intent(this, S_class_dashboard_notes.class)));

        cardQuiz.setOnClickListener(v ->
                startActivity(new Intent(this, S_class_dashboard_quiz.class)));

        cardChat.setOnClickListener(v ->
                startActivity(new Intent(this, S_class_dashboard_chat.class)));

        cardEvents.setOnClickListener(v ->
                startActivity(new Intent(this, S_class_dashboard_events.class)));

        cardAssignment.setOnClickListener(v ->
                startActivity(new Intent(this, S_class_dashboard_assignment.class)));

        cardNotification.setOnClickListener(v ->
                startActivity(new Intent(this, S_class_dashboard_notifications.class)));

    }
}