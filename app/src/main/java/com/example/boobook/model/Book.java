package com.example.boobook.model;

import java.io.Serializable;

public class Book implements Serializable {
    public String id;          // ← Document ID từ Firestore
    public String title;
    public String author;
    public String coverUrl;
    public String genre;
    public long views;
    public long likes = 0;     // ← Số lượt thích, mặc định 0

    public Book() {
        // Firestore cần constructor rỗng
    }
}