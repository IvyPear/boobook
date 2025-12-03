package com.example.boobook.model;

import java.io.Serializable;

public class Book implements Serializable {
    public String id, title, author, coverUrl, genre;
    public long views;

    public Book() {} // Firestore cần constructor rỗng

    public Book(String id, String title, String author, String coverUrl, String genre, long views) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.coverUrl = coverUrl;
        this.genre = genre;
        this.views = views;
    }
}