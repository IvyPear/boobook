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
import com.example.boobook.utils.FavoriteHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
            updateFavoriteIcon(false);
            return;
        }

        DocumentReference favRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.getUid())
                .collection("favorites")
                .document(story.id);

        favRef.get().addOnSuccessListener(snapshot -> {
            if (isAdded()) {
                isFavorite = snapshot.exists();
                updateFavoriteIcon(isFavorite);
            }
        }).addOnFailureListener(e -> {
            if (isAdded()) updateFavoriteIcon(false);
        });
    }

    private void toggleFavorite() {
        if (currentUser == null) {
            showToast("Vui lòng đăng nhập để sử dụng tính năng này");
            return;
        }

        if (story == null || story.id == null || !isAdded()) return;

        // DÙNG FavoriteHelper THAY VÌ TỰ XỬ LÝ
        FavoriteHelper.toggle(
                story.id,
                "story",
                story.title,
                story.coverUrl,
                isNowFavorite -> {
                    if (isAdded()) {
                        isFavorite = isNowFavorite;
                        updateFavoriteIcon(isFavorite);
                        showToast(isNowFavorite ? "Đã thêm vào yêu thích" : "Đã xóa khỏi yêu thích");
                    }
                }
        );
    }

    private void updateFavoriteIcon(boolean isFavorite) {
        binding.btnFavorite.setIconResource(isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart);
    }

    private void showToast(String msg) {
        if (isAdded() && getContext() != null) {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }
}