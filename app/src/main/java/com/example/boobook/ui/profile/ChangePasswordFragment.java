package com.example.boobook.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.boobook.databinding.FragmentChangePasswordBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordFragment extends Fragment {

    private FragmentChangePasswordBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false);

        // Bấm nút back
        binding.ivBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Bấm nút đổi mật khẩu
        binding.btnUpdatePassword.setOnClickListener(v -> changePassword());

        return binding.getRoot();
    }

    private void changePassword() {
        String current = binding.edtCurrentPassword.getText().toString().trim();
        String newPass = binding.edtNewPassword.getText().toString().trim();
        String confirm = binding.edtConfirmPassword.getText().toString().trim();

        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            Snackbar.make(binding.getRoot(), "Vui lòng điền đầy đủ", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (newPass.length() < 6) {
            Snackbar.make(binding.getRoot(), "Mật khẩu mới phải từ 6 ký tự", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirm)) {
            Snackbar.make(binding.getRoot(), "Mật khẩu xác nhận không khớp", Snackbar.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(requireContext(), "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Đang xử lý
        binding.btnUpdatePassword.setEnabled(false);
        binding.btnUpdatePassword.setText("Đang xử lý...");

        // Đổi mật khẩu
        user.updatePassword(newPass)
                .addOnSuccessListener(aVoid -> {
                    Snackbar.make(binding.getRoot(), "Đổi mật khẩu thành công!", Snackbar.LENGTH_LONG).show();
                    requireActivity().onBackPressed();
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(binding.getRoot(), "Lỗi: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                    binding.btnUpdatePassword.setEnabled(true);
                    binding.btnUpdatePassword.setText("Update Password");
                });
    }
}