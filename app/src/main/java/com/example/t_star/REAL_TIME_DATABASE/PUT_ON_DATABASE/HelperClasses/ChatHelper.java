package com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses;

import androidx.annotation.NonNull;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;


public class ChatHelper {

    private static DatabaseReference root() {
        return FirebaseDatabase.getInstance().getReference("Chats");
    }

    // ================= CHAT NODE INIT =================
    public static void createClassChatNode(@NonNull String classId) {
        root().child(classId).keepSynced(true);
    }

    public static void addStudentToClassChat(
            @NonNull String classId,
            @NonNull String studentUid
    ) {
        DatabaseReference ref =
                FirebaseDatabase.getInstance()
                        .getReference("Chats")
                        .child(classId)
                        .child(studentUid);

        Map<String, Object> init = new HashMap<>();
        init.put("lastMessage", "");
        init.put("lastTimestamp", 0);
        init.put("unreadCount", 0);
        init.put("lastSenderId", "");
        init.put("lastDelivered", false);
        init.put("lastSeen", false);

        ref.updateChildren(init);

        // ✅ VERY IMPORTANT
        ref.child("messages").setValue(""); // create messages node
    }


    // ================= CHAT LIST META =================
    public static void updateLastMessage(
            @NonNull String classId,
            @NonNull String studentUid,
            @NonNull String msg,
            @NonNull String senderId
    ) {
        Map<String, Object> map = new HashMap<>();
        map.put("lastMessage", msg);
        map.put("lastTimestamp", ServerValue.TIMESTAMP);
        map.put("lastSenderId", senderId);
        map.put("lastDelivered", false);
        map.put("lastSeen", false);

        root().child(classId).child(studentUid).updateChildren(map);
    }
    // ===== UNREAD COUNTS =====
    public static void incrementUnreadForTeacher(String classId, String studentUid) {
        root().child(classId).child(studentUid)
                .child("unread").child("teacher")
                .setValue(ServerValue.increment(1));
    }

    public static void incrementUnreadForStudent(String classId, String studentUid) {
        root().child(classId).child(studentUid)
                .child("unread").child("student")
                .setValue(ServerValue.increment(1));
    }

    public static void resetTeacherUnread(String classId, String studentUid) {
        root().child(classId).child(studentUid)
                .child("unread").child("teacher")
                .setValue(0);
    }

    public static void resetStudentUnread(String classId, String studentUid) {
        root().child(classId).child(studentUid)
                .child("unread").child("student")
                .setValue(0);
    }

    /*
    // ================= UNREAD COUNT =================
    public static void incrementUnreadCount(
            @NonNull String classId,
            @NonNull String studentUid
    ) {
        root().child(classId)
                .child(studentUid)
                .child("unreadCount")
                .setValue(ServerValue.increment(1));
    }

    public static void resetUnreadCount(
            @NonNull String classId,
            @NonNull String studentUid
    ) {
        root().child(classId)
                .child(studentUid)
                .child("unreadCount")
                .setValue(0);
    }

*/
    // ================= MESSAGE STATUS =================
    public static void markDelivered(String classId, String studentUid, String msgId) {
        DatabaseReference ref = root().child(classId).child(studentUid);
        ref.child("messages").child(msgId).child("delivered").setValue(true);
        ref.child("lastDelivered").setValue(true);
    }

    public static void markSeen(String classId, String studentUid, String msgId) {
        DatabaseReference ref = root().child(classId).child(studentUid);
        ref.child("messages").child(msgId).child("seen").setValue(true);
        ref.child("lastSeen").setValue(true);
    }


}
