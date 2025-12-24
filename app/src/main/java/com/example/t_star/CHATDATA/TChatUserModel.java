package com.example.t_star.CHATDATA;

public class TChatUserModel {

    private String studentUid;
    private String name;
    private String email;

    // chat meta
    private String lastMessage;
    private long lastTimestamp;
    private int unreadCount;

    // ✅ REQUIRED FOR TICKS
    private String lastSenderId;
    private boolean lastDelivered;
    private boolean lastSeen;

    public TChatUserModel() {}

    // ===== GETTERS =====
    public String getStudentUid() { return studentUid; }
    public String getName() { return name != null ? name : "Student"; }
    public String getEmail() { return email != null ? email : ""; }
    public String getLastMessage() { return lastMessage != null ? lastMessage : ""; }
    public long getLastTimestamp() { return lastTimestamp; }
    public int getUnreadCount() { return unreadCount; }

    public String getLastSenderId() { return lastSenderId; }
    public boolean isLastDelivered() { return lastDelivered; }
    public boolean isLastSeen() { return lastSeen; }

    // ===== SETTERS =====
    public void setStudentUid(String studentUid) { this.studentUid = studentUid; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public void setLastTimestamp(long lastTimestamp) { this.lastTimestamp = lastTimestamp; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }

    public void setLastSenderId(String lastSenderId) { this.lastSenderId = lastSenderId; }
    public void setLastDelivered(boolean lastDelivered) { this.lastDelivered = lastDelivered; }
    public void setLastSeen(boolean lastSeen) { this.lastSeen = lastSeen; }
}
