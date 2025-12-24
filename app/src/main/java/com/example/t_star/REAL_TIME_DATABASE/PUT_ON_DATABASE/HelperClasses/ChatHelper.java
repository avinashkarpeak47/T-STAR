package com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class ChatHelper {

    private static DatabaseReference root() {
        return FirebaseDatabase.getInstance().getReference("Chats");
    }

    // =====================================================
    // 1️⃣ CREATE CHAT ROOT WHEN CLASS IS CREATED
    // Called from: TCreateClassActivity (after class created)
    // =====================================================
    public static void createClassChatNode(@NonNull String classId) {
        // Just ensure classId node exists
        root().child(classId).keepSynced(true);
    }
    // 🔥 ONLY FOR RECEIVER
    public static void incrementUnread(@NonNull String classId,
                                       @NonNull String studentUid) {
        root().child(classId).child(studentUid)
                .child("unreadCount")
                .setValue(ServerValue.increment(1));
    }

    public static void resetUnread(@NonNull String classId,
                                   @NonNull String studentUid) {
        root().child(classId).child(studentUid)
                .child("unreadCount").setValue(0);
    }

    // =====================================================
    // 2️⃣ ADD STUDENT NODE WHEN STUDENT JOINS CLASS
    // Called when teacher accepts student
    // =====================================================
    public static void addStudentToClassChat(
            @NonNull String classId,
            @NonNull String studentUid
    ) {
        DatabaseReference ref = root().child(classId).child(studentUid);

        Map<String, Object> init = new HashMap<>();
        init.put("lastMessage", "");
        init.put("lastTimestamp", 0);
        init.put("unreadCount", 0);
        // ❗ messages node auto-create later, do NOT touch it here

        // updateChildren → safe if chat already exists
        ref.updateChildren(init);
    }

    // =====================================================
    // 3️⃣ UPDATE LAST MESSAGE META (ON SEND)
    // =====================================================
    public static void updateLastMessage(
            @NonNull String classId,
            @NonNull String studentUid,
            @NonNull String lastMessage
    ) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("lastMessage", lastMessage);
        updates.put("lastTimestamp", ServerValue.TIMESTAMP);

        root()
                .child(classId)
                .child(studentUid)
                .updateChildren(updates);
    }

    // =====================================================
    // 4️⃣ INCREMENT UNREAD COUNT (RECEIVER SIDE ONLY)
    // =====================================================
    public static void incrementUnreadCount(
            @NonNull String classId,
            @NonNull String studentUid
    ) {
        root()
                .child(classId)
                .child(studentUid)
                .child("unreadCount")
                .setValue(ServerValue.increment(1));
    }

    // =====================================================
    // 5️⃣ RESET UNREAD COUNT (WHEN CHAT ROOM OPENED)
    // =====================================================
    public static void resetUnreadCount(
            @NonNull String classId,
            @NonNull String studentUid
    ) {
        root()
                .child(classId)
                .child(studentUid)
                .child("unreadCount")
                .setValue(0);
    }

    // =====================================================
    // 6️⃣ MARK MESSAGE DELIVERED
    // =====================================================
    public static void markDelivered(
            @NonNull String classId,
            @NonNull String studentUid,
            @NonNull String msgId
    ) {
        root()
                .child(classId)
                .child(studentUid)
                .child("messages")
                .child(msgId)
                .child("delivered")
                .setValue(true);
    }

    // =====================================================
    // 7️⃣ MARK MESSAGE SEEN
    // =====================================================
    public static void markSeen(
            @NonNull String classId,
            @NonNull String studentUid,
            @NonNull String msgId
    ) {
        root()
                .child(classId)
                .child(studentUid)
                .child("messages")
                .child(msgId)
                .child("seen")
                .setValue(true);
    }
}
