package com.example.boobook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.boobook.databinding.ActivityRegisterBinding;
import com.example.boobook.utils.FirebaseHelper;
import com.example.boobook.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        session = new SessionManager(this);

        binding.btnRegister.setOnClickListener(v -> registerUser());
        binding.tvLogin.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "Vui lòng điền đầy đủ và mật khẩu ≥ 6 ký tự!", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(android.view.View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();

                    Map<String, Object> user = new HashMap<>();
                    user.put("name", name);
                    user.put("email", email);
                    user.put("favorites", new ArrayList<String>());
                    user.put("role", null);  // chờ chọn trong RoleSelection
                    user.put("createdAt", System.currentTimeMillis());

                    FirebaseHelper.getInstance().db()
                            .collection("users").document(uid)
                            .set(user)
                            .addOnSuccessListener(aVoid -> {
                                session.login(uid, name, email);  // lưu tạm
                                // CHUYỂN QUA CHỌN VAI TRÒ
                                startActivity(new Intent(RegisterActivity.this, RoleSelectionActivity.class));
                                finish();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Đăng ký thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    binding.progressBar.setVisibility(android.view.View.GONE);
                });
    }
}