package com.example.boobook;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.boobook.ui.HomeFragment;
import com.example.boobook.ui.StoriesFragment;
import com.example.boobook.ui.FavoritesFragment;
import com.example.boobook.ui.profile.ProfileFragment;
import com.example.boobook.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        // MỞ TRANG HOME LẦN ĐẦU
        loadFragment(new HomeFragment());

        // BẤM NÚT DƯỚI → MỞ ĐÚNG TRANG TƯƠNG ỨNG – KHÔNG DÙNG NAVIGATION NỮA!!!
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (id == R.id.nav_stories) {
                fragment = new StoriesFragment();
            } else if (id == R.id.nav_favorites) {
                fragment = new FavoritesFragment();
            } else if (id == R.id.nav_profile) {
                fragment = new ProfileFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });
    }

    // HÀM MỞ FRAGMENT SIÊU ĐƠN GIẢN – HOẠT ĐỘNG 100%
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.navHost, fragment)
                .commit();
    }
}