package com.example.boobook.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.example.boobook.LoginActivity;
import com.example.boobook.R;
import com.example.boobook.databinding.FragmentProfileBinding;
import com.example.boobook.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SessionManager session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        session = new SessionManager(requireContext());

        // Hiển thị tên + email
        binding.tvUserName.setText(session.getName());
        binding.tvUserEmail.setText(session.getEmail());

        // Đếm Favorites realtime
        loadFavoritesCount();

        // Bấm các mục – ĐÃ KẾT NỐI ĐÚNG MÀN HÌNH MỚI
        binding.itemEditProfile.setOnClickListener(v ->
                openFragment(new EditProfileFragment()));

        binding.itemChangePassword.setOnClickListener(v ->
                openFragment(new ChangePasswordFragment()));

        binding.itemReadingHistory.setOnClickListener(v ->
                openFragment(new ReadingHistoryFragment()));

        binding.itemFavorites.setOnClickListener(v -> {
            BottomNavigationView nav = requireActivity().findViewById(R.id.bottomNav);
            if (nav != null) nav.setSelectedItemId(R.id.nav_favorites);
        });

        binding.itemLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            session.logout();
            startActivity(new Intent(requireActivity(), LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            requireActivity().finish();
        });

        return binding.getRoot();
    }

    private void loadFavoritesCount() {
        String uid = session.getUid();
        if (uid == null) return;

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("favorites")
                .addSnapshotListener((snapshot, error) -> {
                    int count = snapshot != null ? snapshot.size() : 0;
                    binding.tvFavoritesCount.setText(String.valueOf(count));
                });
    }

    // Mở fragment mới mượt mà
    private void openFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.navHost, fragment)
                .addToBackStack(null)
                .commit();
    }
}