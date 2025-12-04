// File: model/ContentItem.java
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
    public Object date;           // Timestamp hoặc String
    public String readTime;
    public Long chapterCount = 1L;
    public String[] genreList;

    // THÊM DÒNG NÀY – QUAN TRỌNG NHẤT!!!
    public String category = "all";  // Mặc định là "all", các thể loại: tinhcam, kinhdi, haihuoc, 18+, ...

    public ContentItem() {}

    public String getDateString() {
        if (date == null) return "";
        if (date instanceof Timestamp) {
            Date d = ((Timestamp) date).toDate();
            return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(d);
        }
        return date.toString();
    }

    public long getHotScore() {
        return views + (likes * 2);
    }
}