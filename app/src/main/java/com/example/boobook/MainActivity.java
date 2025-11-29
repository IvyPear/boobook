package com.example.boobook;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;                // DÒNG NÀY BÉ THIẾU NÈ!!!

import com.example.boobook.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(this);

        // BẮT BUỘC KIỂM TRA ĐĂNG NHẬP Ở ĐÂY
        if (!session.isLoggedIn()) {
            // Chưa đăng nhập → đá về Login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Đã đăng nhập → mới cho vào giao diện chính
        setContentView(R.layout.activity_main);

        // Setup BottomNavigation + NavController như cũ
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navHost);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNav, navController);
    }
}