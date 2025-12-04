package com.example.boobook.model;

import java.io.Serializable;

public class Blog implements Serializable {
    public String id;
    public String title;
    public String author;
    public String date;
    public String desc;         // mô tả ngắn trong danh sách
    public String readTime;     // ← DÒNG NÀY BẠN THIẾU (ví dụ: "5 min read")
    public String coverUrl;
    public String content;     // nội dung bài viết dài

    public Blog() {
        // Firestore cần constructor rỗng
    }
}