package com.example.t_star.CHATDATA;

import java.util.List;

public class MessageModel {

    private String senderId;
    private String text;
    private List<String> imageUrls; // 🔥 NEW
    private long timestamp;
    private boolean delivered;
    private boolean seen;

    public MessageModel() {}

    // TEXT MESSAGE
    public MessageModel(String senderId, String text) {
        this.senderId = senderId;
        this.text = text;
    }

    // IMAGE MESSAGE
    public MessageModel(String senderId, List<String> imageUrls) {
        this.senderId = senderId;
        this.imageUrls = imageUrls;
    }

    public String getSenderId() { return senderId; }
    public String getText() { return text == null ? "" : text; }
    public List<String> getImageUrls() { return imageUrls; }
    public long getTimestamp() { return timestamp; }
    public boolean isDelivered() { return delivered; }
    public boolean isSeen() { return seen; }
}

/*
public class MessageModel {
    private String senderId;
    private String text;
    private long timestamp;
    private boolean delivered;
    private boolean seen;

    // 🔥 REQUIRED for Firebase
    public MessageModel() {}

    public MessageModel(String senderId, String text) {
        this.senderId = senderId;
        this.text = text;
        this.delivered = false;
        this.seen = false;
    }

    public String getSenderId() { return senderId; }
    public String getText() { return text; }
    public long getTimestamp() { return timestamp; }
    public boolean isDelivered() { return delivered; }
    public boolean isSeen() { return seen; }

    public void setDelivered(boolean delivered) { this.delivered = delivered; }
    public void setSeen(boolean seen) { this.seen = seen; }
}

 */
