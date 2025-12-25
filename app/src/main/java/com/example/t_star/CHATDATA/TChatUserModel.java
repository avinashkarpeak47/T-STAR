package com.example.t_star.CHATDATA;
public class TChatUserModel {

    private String studentUid;
    private String name;
    private String email;

    private String profileImageUrl; // ✅ NEW

    private String lastMessage;
    private String lastSenderId;
    private boolean lastDelivered;
    private boolean lastSeen;
    private long lastTimestamp;
    private int unreadCount;

    public TChatUserModel() {}

    // ================= GETTERS =================
    public String getStudentUid() { return studentUid == null ? "" : studentUid; }
    public String getName() { return name == null ? "" : name; }
    public String getEmail() { return email == null ? "" : email; }

    public String getProfileImageUrl() { return profileImageUrl; }

    public String getLastMessage() { return lastMessage == null ? "" : lastMessage; }
    public String getLastSenderId() { return lastSenderId == null ? "" : lastSenderId; }
    public boolean isLastDelivered() { return lastDelivered; }
    public boolean isLastSeen() { return lastSeen; }
    public long getLastTimestamp() { return lastTimestamp; }
    public int getUnreadCount() { return unreadCount; }

    // ================= SETTERS =================
    public void setStudentUid(String studentUid) { this.studentUid = studentUid; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public void setLastSenderId(String lastSenderId) { this.lastSenderId = lastSenderId; }
    public void setLastDelivered(boolean lastDelivered) { this.lastDelivered = lastDelivered; }
    public void setLastSeen(boolean lastSeen) { this.lastSeen = lastSeen; }
    public void setLastTimestamp(long lastTimestamp) { this.lastTimestamp = lastTimestamp; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
}
