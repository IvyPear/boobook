package com.example.boobook.model;

import com.google.firebase.Timestamp;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ContentItem implements Serializable {
    public String id;
    public String title;
    public String author;
    public String coverUrl;
    public String desc;
    public Long views = 0L;
    public Long likes = 0L;
    public String type;           // "book" hoặc "story"
    public Object date;           // String hoặc Timestamp
    public String readTime;
    public Long chapterCount = 1L;

    public ContentItem() {}

    public String getDateString() {
        if (date == null) return "";
        if (date instanceof Timestamp) {
            Date d = ((Timestamp) date).toDate();
            return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(d);
        }
        return date.toString();
    }

    // Tính điểm hot (views + likes x 2)
    public long getHotScore() {
        return views + (likes * 2);
    }
}