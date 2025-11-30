package com.example.boobook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.boobook.databinding.ActivityRoleSelectionBinding;
import com.example.boobook.utils.FirebaseHelper;
import com.example.boobook.utils.SessionManager;

public class RoleSelectionActivity extends AppCompatActivity {

    private ActivityRoleSelectionBinding binding;
    private SessionManager session;
    private String selectedRole = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRoleSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        session = new SessionManager(this);

        binding.cardUser.setOnClickListener(v -> selectRole("user"));
        binding.cardAuthor.setOnClickListener(v -> selectRole("author"));
        binding.cardAdmin.setOnClickListener(v -> selectRole("admin"));

        binding.btnContinue.setOnClickListener(v -> saveAndContinue());
    }

    private void selectRole(String role) {
        selectedRole = role;

        // Reset stroke
        binding.cardUser.setStrokeWidth(0);
        binding.cardAuthor.setStrokeWidth(0);
        binding.cardAdmin.setStrokeWidth(0);

        // Highlight
        if (role.equals("user")) {
            binding.cardUser.setStrokeWidth(6);
            binding.cardUser.setStrokeColor(getColor(android.R.color.holo_blue_dark));
            binding.btnContinue.setText("Continue as User");
        } else if (role.equals("author")) {
            binding.cardAuthor.setStrokeWidth(6);
            binding.cardAuthor.setStrokeColor(getColor(android.R.color.holo_blue_dark));
            binding.btnContinue.setText("Continue as Author");
        } else {
            binding.cardAdmin.setStrokeWidth(6);
            binding.cardAdmin.setStrokeColor(getColor(android.R.color.holo_red_dark));
            binding.btnContinue.setText("Continue as Admin");
        }

        updateFeatures();
    }

    private void updateFeatures() {
        binding.featuresContainer.removeAllViews();
        String[] features = selectedRole.equals("user")
                ? new String[]{"Read books", "Add favorites", "Write reviews"}
                : selectedRole.equals("author")
                ? new String[]{"Upload books", "Earn revenue", "All user features"}
                : new String[]{"Full control", "Manage users", "Approve content"};

        for (String f : features) {
            TextView tv = new TextView(this);
            tv.setText("• " + f);
            tv.setTextSize(16);
            tv.setPadding(0, 12, 0, 12);
            binding.featuresContainer.addView(tv);
        }
    }

    private void saveAndContinue() {
        String uid = session.getUid();
        if (uid == null) return;

        FirebaseHelper.getInstance().db()
                .collection("users").document(uid)
                .update("role", selectedRole)
                .addOnSuccessListener(aVoid -> {
                    session.saveRole(selectedRole);
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                });
    }
}