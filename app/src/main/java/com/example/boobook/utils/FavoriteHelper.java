package com.example.boobook.utils;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class FavoriteHelper {

    // THÊM THAM SỐ type: "book" hoặc "story"
    public static void toggle(String contentId, String type, String title, String coverUrl, OnResult listener) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (uid == null) {
            Log.e("FavoriteHelper", "User not logged in!");
            return;
        }

        var ref = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("favorites")
                .document(contentId);

        ref.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                // Đã thích → xóa
                ref.delete().addOnSuccessListener(aVoid -> listener.onResult(false))
                        .addOnFailureListener(e -> Log.e("FavoriteHelper", "Delete error: " + e));
            } else {
                // Chưa thích → thêm với ĐẦY ĐỦ THÔNG TIN
                Map<String, Object> data = new HashMap<>();
                data.put("contentId", contentId);
                data.put("type", type);  // "book" hoặc "story"
                data.put("title", title);
                data.put("coverUrl", coverUrl);
                data.put("addedAt", FieldValue.serverTimestamp());
                data.put("time", System.currentTimeMillis());

                ref.set(data)
                        .addOnSuccessListener(aVoid -> listener.onResult(true))
                        .addOnFailureListener(e -> Log.e("FavoriteHelper", "Add error: " + e));
            }
        }).addOnFailureListener(e -> {
            Log.e("FavoriteHelper", "Check favorite error: " + e);
        });
    }

    public interface OnResult {
        void onResult(boolean isNowFavorite); // true = vừa thêm, false = vừa xóa
    }
}