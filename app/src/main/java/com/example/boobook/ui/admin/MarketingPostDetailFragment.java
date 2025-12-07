package com.example.boobook.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.boobook.R;
import com.example.boobook.databinding.FragmentMarketingPostDetailBinding;
import com.example.boobook.model.MarketingPost;
import com.example.boobook.utils.RoleHelper;
import com.google.firebase.firestore.FirebaseFirestore;

public class MarketingPostDetailFragment extends Fragment {

    private FragmentMarketingPostDetailBinding binding;
    private MarketingPost post;
    private String postId;

    // Factory method - chỉ truyền ID
    public static MarketingPostDetailFragment newInstance(String postId) {
        MarketingPostDetailFragment fragment = new MarketingPostDetailFragment();
        Bundle args = new Bundle();
        args.putString("postId", postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMarketingPostDetailBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            postId = getArguments().getString("postId");
            loadPostFromFirestore();
        }

        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        // Kiểm tra quyền để hiển thị nút Edit
        RoleHelper.getCurrentUserRole(role -> {
            if (RoleHelper.isAuthorOrAdmin(role)) {
                binding.btnEditCampaign.setVisibility(View.VISIBLE);
                binding.btnEditCampaign.setOnClickListener(v -> editCampaign());
            } else {
                binding.btnEditCampaign.setVisibility(View.GONE);
            }
        });

        binding.btnJoin.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Joining campaign...", Toast.LENGTH_SHORT).show();
        });

        return binding.getRoot();
    }

    private void loadPostFromFirestore() {
        if (postId == null || postId.isEmpty()) return;

        FirebaseFirestore.getInstance()
                .collection("marketing_posts")
                .document(postId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        post = documentSnapshot.toObject(MarketingPost.class);
                        if (post != null) {
                            post.setId(documentSnapshot.getId());
                            bindData();
                        }
                    }
                });
    }

    private void bindData() {
        if (post == null) return;

        // Set tiêu đề và mô tả
        binding.tvTitle.setText(post.getTitle());
        binding.tvDescription.setText(post.getDescription());

        // Load ảnh
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(post.getImageUrl())
                    .placeholder(R.drawable.img_marketing_placeholder)
                    .into(binding.ivPostImage);
        }

        // Update Active tag
        if (post.isActive()) {
            binding.tvActiveTag.setVisibility(View.VISIBLE);
        } else {
            binding.tvActiveTag.setVisibility(View.GONE);
        }

        // Update các thông số
        binding.tvTotalViews.setText(formatNumber(post.getViews()));
        // Các giá trị khác có thể lấy từ Firestore hoặc tính toán
        // binding.tvClicks.setText("...");
        // binding.tvShares.setText("...");
        // binding.tvCtr.setText("...");
    }

    private void editCampaign() {
        if (post != null) {
            Toast.makeText(requireContext(), "Edit campaign: " + post.getTitle(), Toast.LENGTH_SHORT).show();
            // TODO: Mở fragment/edit dialog để chỉnh sửa campaign
        }
    }

    private String formatNumber(long number) {
        if (number >= 1000000) {
            return String.format("%.1fM", number / 1000000.0);
        } else if (number >= 1000) {
            return String.format("%.1fK", number / 1000.0);
        } else {
            return String.valueOf(number);
        }
    }
}