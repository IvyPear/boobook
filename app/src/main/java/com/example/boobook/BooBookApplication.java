package com.example.boobook;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class BooBookApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}