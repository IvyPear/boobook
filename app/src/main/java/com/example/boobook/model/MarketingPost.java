package com.example.boobook.model;

import java.io.Serializable;

public class MarketingPost implements Serializable {
    private String id;
    private String title;
    private String description;
    private String imageUrl;
    private boolean active;  // Đổi từ isActive thành active
    private long createdAt;
    private long views;  // Thêm field views

    // Constructor rỗng cho Firestore
    public MarketingPost() {}

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getViews() { return views; }
    public void setViews(long views) { this.views = views; }
}