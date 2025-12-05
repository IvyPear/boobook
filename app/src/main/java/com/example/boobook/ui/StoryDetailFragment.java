package com.example.boobook.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.boobook.R;
import com.example.boobook.databinding.FragmentStoryDetailBinding;
import com.example.boobook.model.Story;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class StoryDetailFragment extends Fragment {

    private FragmentStoryDetailBinding binding;
    private Story story;
    private FirebaseUser currentUser;
    private boolean isFavorite = false;

    public static StoryDetailFragment newInstance(Story story) {
        StoryDetailFragment f = new StoryDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("story", story);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStoryDetailBinding.inflate(inflater, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (getArguments() != null) {
            story = (Story) getArguments().getSerializable("story");
            bindData();
            checkFavoriteStatus();
        }

        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        binding.btnFavorite.setOnClickListener(v -> toggleFavorite());

        return binding.getRoot();
    }

    private void bindData() {
        binding.tvTitle.setText(story.title);
        binding.tvAuthor.setText(story.author);
        binding.tvAuthorInitials.setText(getInitials(story.author));

        String dateTime = story.getDateString();
        if (story.readTime != null && !story.readTime.isEmpty()) {
            dateTime += " • " + story.readTime;
        }
        binding.tvDateReadTime.setText(dateTime);
        binding.tvContent.setText(story.content);

        Glide.with(this)
                .load(story.coverUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_close_clear_cancel)
                .into(binding.ivBlogCover);
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isEmpty()) {
                sb.append(Character.toUpperCase(p.charAt(0)));
                if (sb.length() >= 2) break;
            }
        }
        return sb.length() > 0 ? sb.toString() : "?";
    }

    private void checkFavoriteStatus() {
        if (currentUser == null || story == null || story.id == null) {
            binding.btnFavorite.setIconResource(R.drawable.ic_heart);
            isFavorite = false;
            return;
        }

        DocumentReference favRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.getUid())
                .collection("favorites")
                .document(story.id);

        favRef.get().addOnSuccessListener(snapshot -> {
            if (!isAdded()) return; // Chống crash nếu Fragment đã chết
            isFavorite = snapshot.exists();
            binding.btnFavorite.setIconResource(isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart);
        }).addOnFailureListener(e -> {
            if (isAdded()) binding.btnFavorite.setIconResource(R.drawable.ic_heart);
        });
    }

    // HÀM QUAN TRỌNG NHẤT – ĐÃ HOÀN HẢO TUYỆT ĐỐI
    private void toggleFavorite() {
        if (currentUser == null) {
            showToast("Vui lòng đăng nhập để sử dụng tính năng này");
            return;
        }

        if (story == null || story.id == null || !isAdded()) return;

        // ĐẢO NGƯỢC TRẠNG THÁI NGAY LẬP TỨC – UI PHẢN HỒI TỨC THÌ
        isFavorite = !isFavorite;
        binding.btnFavorite.setIconResource(isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart);

        DocumentReference favRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.getUid())
                .collection("favorites")
                .document(story.id);

        if (isFavorite) {
            // ĐANG THÊM VÀO YÊU THÍCH
            Map<String, Object> data = new HashMap<>();
            data.put("addedAt", System.currentTimeMillis());
            data.put("type", "story");
            data.put("title", story.title);
            data.put("coverUrl", story.coverUrl);

            favRef.set(data)
                    .addOnSuccessListener(aVoid -> {
                        if (isAdded()) showToast("Đã thêm vào yêu thích");
                    })
                    .addOnFailureListener(e -> {
                        if (isAdded()) {
                            isFavorite = false;
                            binding.btnFavorite.setIconResource(R.drawable.ic_heart);
                            showToast("Lỗi mạng, không thể thêm");
                        }
                    });
        } else {
            // ĐANG XÓA KHỎI YÊU THÍCH
            favRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        if (isAdded()) showToast("Đã xóa khỏi yêu thích");
                    })
                    .addOnFailureListener(e -> {
                        if (isAdded()) {
                            isFavorite = true;
                            binding.btnFavorite.setIconResource(R.drawable.ic_heart_filled);
                            showToast("Lỗi mạng, không thể xóa");
                        }
                    });
        }
    }

    private void showToast(String msg) {
        if (isAdded() && getContext() != null) {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }
}