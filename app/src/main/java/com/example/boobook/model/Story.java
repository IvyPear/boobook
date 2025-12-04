package com.example.boobook.model;

import com.google.firebase.Timestamp;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Story implements Serializable {

    public String id;
    public String title;
    public String author;
    public Object date;           // ← LINH HOẠT: String hoặc Timestamp
    public String readTime;
    public String desc;
    public String coverUrl;
    public String content;
    public Long likes;

    public Story() {} // Firestore cần constructor rỗng

    // HÀM SIÊU QUAN TRỌNG – ĐÂY LÀ CÁI ADAPTER ĐANG TÌM!
    public String getDateString() {
        if (date == null) return "Không rõ";

        if (date instanceof Timestamp) {
            Date d = ((Timestamp) date).toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return sdf.format(d);
        } else if (date instanceof String) {
            return (String) date;
        }
        return date.toString();
    }
}