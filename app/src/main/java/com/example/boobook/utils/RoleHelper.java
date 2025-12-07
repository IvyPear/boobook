package com.example.boobook.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RoleHelper {

    public interface RoleCallback {
        void onResult(String role); // "admin", "author", "user"
    }

    public static void getCurrentUserRole(RoleCallback callback) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (uid == null) {
            callback.onResult("user");
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    String role = doc.getString("role");
                    callback.onResult(role != null ? role : "user");
                })
                .addOnFailureListener(e -> callback.onResult("user"));
    }

    public static boolean isAdmin(String role) {
        return "admin".equals(role);
    }

    public static boolean isAuthorOrAdmin(String role) {
        return "author".equals(role) || "admin".equals(role);
    }
}