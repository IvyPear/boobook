package com.example.boobook.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.boobook.databinding.FragmentEditProfileBinding;
import com.example.boobook.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);

        SessionManager session = new SessionManager(requireContext());
        binding.etName.setText(session.getName()); // Lấy tên từ Session

        binding.btnSave.setOnClickListener(v -> {
            String newName = binding.etName.getText().toString().trim();
            if (newName.isEmpty()) {
                Toast.makeText(requireContext(), "Nhập tên đi anh ơi", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.btnSave.setEnabled(false);

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build();

            FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates)
                    .addOnSuccessListener(aVoid -> {
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(uid)
                                .update("name", newName)
                                .addOnSuccessListener(a -> {
                                    // DÒNG QUAN TRỌNG NHẤT – CẬP NHẬT SESSION!!!
                                    session.saveName(newName);

                                    Toast.makeText(requireContext(), "Đổi tên thành công!", Toast.LENGTH_LONG).show();
                                    requireActivity().onBackPressed();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        binding.btnSave.setEnabled(true);
                    });
        });

        return binding.getRoot();
    }
}