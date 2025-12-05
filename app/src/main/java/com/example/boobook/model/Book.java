package com.example.boobook.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class Book implements Serializable {

    public String id;                    // Document ID
    public String title;
    public String author;
    public String coverUrl;
    public String genre;
    public long views = 0;
    public long likes = 0;

    // THÊM 2 FIELD NÀY ĐỂ FIRESTORE KHÔNG CÒN BÁO LỖI NỮA
    @ServerTimestamp
    public Date createdAt;               // Tự động sinh thời gian khi tạo trên server

    @Exclude                             // Không lưu field này lên Firestore (chỉ để dùng local
    public boolean isFavorite = false;   // Dùng để đánh dấu đã yêu thích chưa (cho adapter)

    // Constructor rỗng bắt buộc cho Firestore
    public Book() {}

    // Constructor tiện lợi (tùy chọn)
    public Book(String title, String author, String coverUrl, String genre) {
        this.title = title;
        this.author = author;
        this.coverUrl = coverUrl;
        this.genre = genre;
    }

    // Getter cho id (rất quan trọng khi dùng với Favorites)
    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}