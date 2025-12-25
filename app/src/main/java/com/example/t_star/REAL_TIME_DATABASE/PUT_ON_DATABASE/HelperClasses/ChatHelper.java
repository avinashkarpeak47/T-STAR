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
        Map<String, Object> init = new HashMap<>();
        init.put("lastMessage", "");
        init.put("lastTimestamp", 0);
        init.put("unreadCount", 0);
        init.put("lastSenderId", "");
        init.put("lastDelivered", false);
        init.put("lastSeen", false);

        root().child(classId).child(studentUid).updateChildren(init);
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

    // ================= MESSAGE STATUS =================
    public static void markDelivered(
            @NonNull String classId,
            @NonNull String studentUid,
            @NonNull String msgId
    ) {
        root().child(classId)
                .child(studentUid)
                .child("messages")
                .child(msgId)
                .child("delivered")
                .setValue(true);

        root().child(classId)
                .child(studentUid)
                .child("lastDelivered")
                .setValue(true);
    }

    public static void markSeen(
            @NonNull String classId,
            @NonNull String studentUid,
            @NonNull String msgId
    ) {
        root().child(classId)
                .child(studentUid)
                .child("messages")
                .child(msgId)
                .child("seen")
                .setValue(true);

        root().child(classId)
                .child(studentUid)
                .child("lastSeen")
                .setValue(true);
    }
}
