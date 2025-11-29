package com.example.boobook.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    public String name;
    public String email;
    public String role;           // "User", "Author", "Admin"
    public List<String> favorites = new ArrayList<>();

    public User() {} // Bắt buộc cho Firestore

    public User(String name, String email, String role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }
}