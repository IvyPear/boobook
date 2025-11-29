package com.example.boobook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.boobook.databinding.ActivityRegisterBinding;
import com.example.boobook.model.User;
import com.example.boobook.utils.FirebaseHelper;
import com.example.boobook.utils.SessionManager;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        session = new SessionManager(this);

        binding.btnRegister.setOnClickListener(v -> {
            String name = binding.tilName.getEditText().getText().toString().trim();
            String email = binding.tilEmail.getEditText().getText().toString().trim();
            String pass = binding.tilPassword.getEditText().getText().toString();
            String confirm = binding.tilConfirm.getEditText().getText().toString();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!pass.equals(confirm)) {
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseHelper.getInstance().auth()
                    .createUserWithEmailAndPassword(email, pass)
                    .addOnSuccessListener(authResult -> {
                        String uid = authResult.getUser().getUid();
                        User newUser = new User(name, email, "User");

                        FirebaseHelper.getInstance().db()
                                .collection("users").document(uid)
                                .set(newUser)
                                .addOnSuccessListener(aVoid -> {
                                    session.login(name, email, "User", uid);
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Lỗi tạo dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Đăng ký thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        binding.tvLogin.setOnClickListener(v -> finish());
    }
}