package com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Assignment_Helper {

    private static final String TAG = "ASSIGNMENT_HELPER";

    // ---------------------------------------------------------------------
    // GET SUBMITTED STUDENT UID LIST (FINAL PATH)
    //
    // Assignments
    //   └── classId
    //        └── assignmentId
    //              └── submissions
    //                    └── studentUid
    // ---------------------------------------------------------------------
    public static void getAssignmentStudentStatus(
            String classId,
            String assignmentId,
            AssignmentStatusCallback cb) {

        // ✅ SAFETY CHECK
        if (classId == null || assignmentId == null) {
            Log.e(TAG, "❌ Null detected → classId=" + classId
                    + ", assignmentId=" + assignmentId);
            cb.onLoaded(new ArrayList<>());
            return;
        }

        Log.d(TAG, "✅ Fetching submissions → classId="
                + classId + ", assignmentId=" + assignmentId);

        FirebaseDatabase.getInstance()
                .getReference("Assignments")
                .child(classId)
                .child(assignmentId)
                .child("submissions")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {

                        ArrayList<String> submittedList = new ArrayList<>();

                        if (snap.exists()) {
                            for (DataSnapshot s : snap.getChildren()) {
                                String studentUid = s.getKey();
                                if (studentUid != null) {
                                    submittedList.add(studentUid);
                                }
                            }
                        }

                        Log.d(TAG,
                                "📌 Submitted students = " + submittedList.size());

                        cb.onLoaded(submittedList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG,
                                "❌ Firebase error: " + error.getMessage());
                        cb.onLoaded(new ArrayList<>());
                    }
                });
    }

    // ---------------------------------------------------------------------
    // CALLBACK
    // ---------------------------------------------------------------------
    public interface AssignmentStatusCallback {
        void onLoaded(ArrayList<String> submittedList);
    }
}
