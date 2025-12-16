package com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses;

import androidx.annotation.NonNull;

import com.google.firebase.database.*;

import java.util.ArrayList;

public class Assignment_Helper {

    // ---------------------------------------------------------------------
    // GET SUBMITTED STUDENT LIST FOR ONE ASSIGNMENT
    // Path: AssignmentSubmissions → classId → assignmentId → uid: true
    // ---------------------------------------------------------------------
    public static void getAssignmentStudentStatus(
            String classId,
            String assignmentId,
            AssignmentStatusCallback cb) {

        FirebaseDatabase.getInstance().getReference("AssignmentSubmissions")
                .child(classId)
                .child(assignmentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {

                        ArrayList<String> submittedList = new ArrayList<>();

                        for (DataSnapshot s : snap.getChildren()) {
                            submittedList.add(s.getKey());  // student UID
                        }

                        cb.onLoaded(submittedList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cb.onLoaded(new ArrayList<>());
                    }
                });
    }

    // Callback
    public interface AssignmentStatusCallback {
        void onLoaded(ArrayList<String> submittedList);
    }
}
