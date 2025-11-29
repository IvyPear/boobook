// File: app/src/main/java/com/example/boobook/LoginActivity.java
package com.example.boobook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.boobook.databinding.ActivityLoginBinding;
import com.example.boobook.utils.FirebaseHelper;
import com.example.boobook.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        session = new SessionManager(this);

        // KIỂM TRA THẬT QUA FIREBASE
        if (FirebaseHelper.getInstance().auth().getCurrentUser() != null && session.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Nếu không có user → xóa session cũ
        session.logout();

        binding.btnLogin.setOnClickListener(v -> login());
        binding.tvRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void login() {
        String email = binding.tilEmail.getEditText().getText().toString().trim();
        String password = binding.tilPassword.getEditText().getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseHelper.getInstance().auth()
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();

                    FirebaseHelper.getInstance().db()
                            .collection("users").document(uid)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                String name = documentSnapshot.getString("name");
                                if (name == null || name.isEmpty()) name = "User";

                                session.saveUser(name, email, uid);

                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            });
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Đăng nhập thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}