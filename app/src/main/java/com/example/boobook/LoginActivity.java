package com.example.boobook;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.boobook.databinding.ActivityLoginBinding;
import com.example.boobook.utils.FirebaseHelper;
import com.example.boobook.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        session = new SessionManager(this);

        // Nếu đã đăng nhập → vào Main luôn
        if (session.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(v -> performLogin());

        binding.tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        binding.tvForgot.setOnClickListener(v -> {
            Toast.makeText(this, "Tính năng quên mật khẩu sắp ra mắt!", Toast.LENGTH_SHORT).show();
        });
    }

    // ... (phần đầu giữ nguyên)

    private void performLogin() {
        String email = binding.tilEmail.getEditText().getText().toString().trim();
        String password = binding.tilPassword.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ!", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnLogin.setEnabled(false);
        binding.btnLogin.setText("Đang đăng nhập...");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();

                    FirebaseHelper.getInstance().db()
                            .collection("users").document(uid)
                            .get()
                            .addOnSuccessListener(document -> {
                                String name = document.getString("name");
                                String role = document.getString("role");
                                if (role == null) role = "user";

                                // ĐÚNG 3 THAM SỐ → KHÔNG LỖI NỮA
                                session.login(uid, name != null ? name : "User", email);
                                session.saveRole(role);

                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Sai email hoặc mật khẩu!", Toast.LENGTH_LONG).show();
                    binding.btnLogin.setEnabled(true);
                    binding.btnLogin.setText("Login");
                });
    }
}