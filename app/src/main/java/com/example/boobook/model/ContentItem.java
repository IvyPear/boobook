// model/ContentItem.java
package com.example.boobook.model;

import java.io.Serializable;

public class ContentItem implements Serializable {
    public String id;
    public String title;
    public String author;
    public String date;
    public String desc;
    public String readTime;
    public String coverUrl;
    public String content;
    public String type;        // ← "book" hoặc "blog"

    public ContentItem() {}
}