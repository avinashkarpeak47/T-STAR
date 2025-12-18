package com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.ModelClasses.User_Authentication_Model;
import com.example.t_star.TEACHERACTIVITIES.CLASSES.TClassesModel;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class Classes_helper {

    // ------------------------------------------------------------
    // DATABASE REFERENCE
    // ------------------------------------------------------------
    private static DatabaseReference classesRef() {
        return FirebaseDatabase.getInstance().getReference("Classes");
    }

    // ------------------------------------------------------------
    // CREATE CLASS
    // ------------------------------------------------------------
    public static void createClass(TClassesModel model, DatabaseCallback cb) {
        classesRef().child(model.getClassId())
                .setValue(model)
                .addOnSuccessListener(a -> cb.onSuccess())
                .addOnFailureListener(cb::onFailure);
    }

    // ------------------------------------------------------------
    // GET ALL CLASSES
    // ------------------------------------------------------------
    public static void getAllClasses(AllClassesCallback cb) {

        classesRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<TClassesModel> list = new ArrayList<>();

                for (DataSnapshot data : snapshot.getChildren()) {

                    TClassesModel model = new TClassesModel();
                    model.setClassId(data.child("classId").getValue(String.class));
                    model.setClassName(data.child("className").getValue(String.class));
                    model.setDescription(data.child("description").getValue(String.class));
                    model.setTeacherId(data.child("teacherId").getValue(String.class));
                    model.setTeacherName(data.child("teacherName").getValue(String.class));

                    Long count = data.child("studentsCount").getValue(Long.class);
                    model.setStudentsCount(count != null ? count.intValue() : 0);

                    list.add(model);
                }

                cb.onLoaded(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                cb.onLoaded(new ArrayList<>());
            }
        });
    }

    // ------------------------------------------------------------
    // GET STUDENTS OF A CLASS (UID LIST)
    // ------------------------------------------------------------
    public static void getClassStudents(String classId, StudentListCallback cb) {

        Log.d("DEBUG_STUDENT", "Fetching students for classId = " + classId);

        classesRef().child(classId).child("students")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        ArrayList<String> uids = new ArrayList<>();

                        for (DataSnapshot s : snapshot.getChildren()) {
                            uids.add(s.getKey()); // UID is KEY
                        }

                        cb.onLoaded(uids);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cb.onLoaded(new ArrayList<>());
                    }
                });
    }

    // ------------------------------------------------------------
    // ADD STUDENT TO CLASS
    // ------------------------------------------------------------
    public static void addStudentToClass(String classId, String uid) {
        classesRef().child(classId)
                .child("students")
                .child(uid)
                .setValue(true);

        updateStudentCount(classId);
    }

    // ------------------------------------------------------------
    // REMOVE STUDENT FROM CLASS
    // ------------------------------------------------------------
    public static void removeStudentFromClass(String classId, String uid) {
        classesRef().child(classId)
                .child("students")
                .child(uid)
                .removeValue();

        updateStudentCount(classId);
    }

    // ------------------------------------------------------------
    // UPDATE STUDENT COUNT
    // ------------------------------------------------------------
    public static void updateStudentCount(String classId) {
        classesRef().child(classId).child("students")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        classesRef().child(classId)
                                .child("studentsCount")
                                .setValue(snapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    // ------------------------------------------------------------
    // USER DETAILS FETCH
    // ------------------------------------------------------------
    public static class Users_Helper {

        public static void getUserDetails(String uid, UserCallback cb) {
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            cb.onUserLoaded(
                                    snapshot.getValue(User_Authentication_Model.class)
                            );
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            cb.onUserLoaded(null);
                        }
                    });
        }

        public interface UserCallback {
            void onUserLoaded(User_Authentication_Model user);
        }
    }

    // ------------------------------------------------------------
    // CALLBACK INTERFACES
    // ------------------------------------------------------------
    public interface DatabaseCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface AllClassesCallback {
        void onLoaded(ArrayList<TClassesModel> list);
    }

    public interface StudentListCallback {
        void onLoaded(ArrayList<String> uids);
    }
}
