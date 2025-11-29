// File: app/src/main/java/com/example/boobook/utils/SessionManager.java
package com.example.boobook.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "BooBookPrefs";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // CÁCH MỚI
    public void saveUser(String name, String email, String uid) {
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_UID, uid);
        editor.apply();
    }

    // CÁCH CŨ – GIỮ LẠI ĐỂ KHÔNG LỖI
    public void login(String name, String email, String role, String uid) {
        saveUser(name, email, uid); // gọi lại saveUser là đủ
    }

    public void logout() {
        editor.clear().apply();
    }

    public boolean isLoggedIn() {
        return getUid() != null;
    }

    public String getName()  { return prefs.getString(KEY_NAME, "User"); }
    public String getEmail() { return prefs.getString(KEY_EMAIL, "user@gmail.com"); }
    public String getUid()   { return prefs.getString(KEY_UID, null); }
}