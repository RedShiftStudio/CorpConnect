package com.company.corpconnect.model;

public class Notification {
    private String type;
    private String text;
    private String time;
    private boolean isRead;

    public Notification(String type, String text, String time, boolean isRead) {
        this.type = type;
        this.text = text;
        this.time = time;
        this.isRead = isRead;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
