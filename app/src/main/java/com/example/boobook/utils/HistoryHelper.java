package com.example.boobook.utils;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class HistoryHelper {

    // Lưu lịch sử đọc sách
    public static void saveBookHistory(String bookId, String bookTitle, String coverUrl, String author) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (uid == null) {
            Log.d("HistoryHelper", "User not logged in, skipping history save");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("contentId", bookId);
        data.put("title", bookTitle);
        data.put("coverUrl", coverUrl);
        data.put("author", author);
        data.put("type", "book");
        data.put("readAt", FieldValue.serverTimestamp());
        data.put("timestamp", System.currentTimeMillis());

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("readHistory")
                .document(bookId)
                .set(data)
                .addOnSuccessListener(aVoid -> Log.d("HistoryHelper", "Book history saved: " + bookTitle))
                .addOnFailureListener(e -> Log.e("HistoryHelper", "Error saving book history", e));
    }

    // Lưu lịch sử đọc truyện ngắn
    public static void saveStoryHistory(String storyId, String storyTitle, String coverUrl, String author) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (uid == null) {
            Log.d("HistoryHelper", "User not logged in, skipping history save");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("contentId", storyId);
        data.put("title", storyTitle);
        data.put("coverUrl", coverUrl);
        data.put("author", author);
        data.put("type", "story");
        data.put("readAt", FieldValue.serverTimestamp());
        data.put("timestamp", System.currentTimeMillis());

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("readHistory")
                .document(storyId)
                .set(data)
                .addOnSuccessListener(aVoid -> Log.d("HistoryHelper", "Story history saved: " + storyTitle))
                .addOnFailureListener(e -> Log.e("HistoryHelper", "Error saving story history", e));
    }
}