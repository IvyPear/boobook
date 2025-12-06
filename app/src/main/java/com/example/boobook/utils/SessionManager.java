package com.example.boobook.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "BooBookSession";
    private static final String KEY_UID = "uid";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // ĐĂNG NHẬP – LƯU ĐỦ THÔNG TIN
    public void login(String uid, String name, String email) {
        editor.putString(KEY_UID, uid);
        editor.putString(KEY_NAME, name != null ? name : "User");
        editor.putString(KEY_EMAIL, email != null ? email : "");
        editor.apply();
    }

    // THÊM ROLE (nếu cần)
    public void saveRole(String role) {
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    // HÀM MỚI: CẬP NHẬT TÊN KHI ĐỔI TRONG EDIT PROFILE
    public void saveName(String name) {
        editor.putString(KEY_NAME, name != null ? name : "User");
        editor.apply();
    }

    // LẤY THÔNG TIN
    public String getUid() {
        return pref.getString(KEY_UID, null);
    }

    public String getName() {
        return pref.getString(KEY_NAME, "User");
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    public String getRole() {
        return pref.getString(KEY_ROLE, "user");
    }

    public boolean isLoggedIn() {
        return getUid() != null;
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}