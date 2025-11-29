package com.example.boobook.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.boobook.LoginActivity;
import com.example.boobook.R;
import com.example.boobook.databinding.FragmentProfileBinding;
import com.example.boobook.utils.FirebaseHelper;
import com.example.boobook.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SessionManager session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        session = new SessionManager(requireContext());

        // HIỆN THÔNG TIN THẬT
        binding.tvUserName.setText(session.getName());
        binding.tvUserEmail.setText(session.getEmail());

        // ĐẾM FAVORITES THẬT
        loadFavoritesCount();

        // LOGOUT + FAVORITES CLICK
        binding.getRoot().post(() -> {
            setItemClick("Logout", v -> {
                FirebaseHelper.getInstance().auth().signOut();
                session.logout();
                startActivity(new Intent(requireActivity(), LoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                requireActivity().finish();
            });

            setItemClick("Favorites", v -> {
                BottomNavigationView nav = requireActivity().findViewById(R.id.bottomNav);
                if (nav != null) nav.setSelectedItemId(R.id.nav_favorites);
            });
        });

        return binding.getRoot();
    }

    private void loadFavoritesCount() {
        String uid = session.getUid();
        if (uid == null || uid.isEmpty()) return;

        FirebaseHelper.getInstance().db()
                .collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    List<?> favs = (List<?>) doc.get("favorites");
                    int count = favs != null ? favs.size() : 0;
                    replaceNumber("0", String.valueOf(count));
                });
    }

    private void setItemClick(String text, View.OnClickListener listener) {
        findTextView(binding.getRoot(), text, listener);
    }

    private void findTextView(View view, String target, View.OnClickListener listener) {
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                if (child instanceof TextView && target.equals(((TextView) child).getText().toString().trim())) {
                    View parent = (View) child.getParent();
                    parent.setOnClickListener(listener);
                    return;
                }
                findTextView(child, target, listener);
            }
        }
    }

    private void replaceNumber(String oldNum, String newNum) {
        replaceInView(binding.getRoot(), oldNum, newNum);
    }

    private void replaceInView(View view, String oldT, String newT) {
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View v = vg.getChildAt(i);
                if (v instanceof TextView && oldT.equals(((TextView) v).getText().toString())) {
                    ((TextView) v).setText(newT);
                }
                replaceInView(v, oldT, newT);
            }
        }
    }
}