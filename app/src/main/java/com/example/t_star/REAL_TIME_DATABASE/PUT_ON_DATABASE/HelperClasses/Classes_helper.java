package com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses;

import androidx.annotation.NonNull;

import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.ModelClasses.User_Authentication_Model;
import com.example.t_star.TEACHERACTIVITIES.CLASSES.TClassesModel;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class Classes_helper {

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
                    TClassesModel model = data.getValue(TClassesModel.class);

                    if (model != null) list.add(model);
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
    // GET STUDENTS OF A CLASS
    // Returns UID list of students
    // ------------------------------------------------------------
    public static void getClassStudents(String classId, StudentListCallback cb) {

        classesRef().child(classId).child("students")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        ArrayList<String> uids = new ArrayList<>();

                        for (DataSnapshot s : snapshot.getChildren()) {
                            String uid = s.getValue(String.class);
                            if (uid != null) uids.add(uid);
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

        classesRef().child(classId).child("students")
                .child(uid)
                .setValue(uid);

        updateStudentCount(classId);
    }

    // ------------------------------------------------------------
    // REMOVE STUDENT FROM CLASS
    // ------------------------------------------------------------
    public static void removeStudentFromClass(String classId, String uid) {

        classesRef().child(classId).child("students")
                .child(uid)
                .removeValue();

        updateStudentCount(classId);
    }

    // ------------------------------------------------------------
    // UPDATE STUDENT COUNT (Automatic)
    // ------------------------------------------------------------
    public static void updateStudentCount(String classId) {

        classesRef().child(classId).child("students")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        int count = (int) snapshot.getChildrenCount();

                        classesRef().child(classId)
                                .child("studentsCount")
                                .setValue(count);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    // ------------------ CALLBACK INTERFACES --------------------

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

    public static class Users_Helper {

        public static void getUserDetails(String uid, UserCallback cb) {

            FirebaseDatabase.getInstance().getReference("Users")
                    .child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User_Authentication_Model user = snapshot.getValue(User_Authentication_Model.class);
                            cb.onUserLoaded(user);
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
}
