package com.company.corpconnect.model;

import java.io.Serializable;

public class News implements Serializable {
    private String id;
    private String title;
    private String description;
    private String author;
    private String imageUrl;
    private String date;

    public News(String id, String title, String description, String author, String imageUrl, String date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.author = author;
        this.imageUrl = imageUrl;
        this.date = date;
    }

    public News() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNewsId() {
        return id;
    }

    public void setNewsId(String newsId) {
        this.id = newsId;
    }
}
