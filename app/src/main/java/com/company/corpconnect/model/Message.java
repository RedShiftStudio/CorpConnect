package com.company.corpconnect.model;

public class Message {
    private String senderId;
    private String text;
    private long timestamp;

    public Message(String senderId, String text, long timestamp) {
        this.senderId = senderId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public Message() {
    }

    public String getSenderId() {
        return senderId;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
