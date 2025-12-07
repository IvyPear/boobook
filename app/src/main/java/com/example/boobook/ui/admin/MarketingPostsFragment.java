package com.example.boobook.ui.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.boobook.R;
import com.example.boobook.adapter.MarketingPostAdapter;
import com.example.boobook.databinding.FragmentMarketingPostsBinding;
import com.example.boobook.model.MarketingPost;
import com.example.boobook.utils.RoleHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class MarketingPostsFragment extends Fragment {

    private FragmentMarketingPostsBinding binding;
    private MarketingPostAdapter adapter;
    private List<MarketingPost> allPosts = new ArrayList<>();
    private List<MarketingPost> filteredPosts = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMarketingPostsBinding.inflate(inflater, container, false);

        setupRecyclerView();
        loadAllPosts();

        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        // SEARCH - Sửa đúng cách lấy EditText
        if (binding.tilSearch != null) {
            binding.tilSearch.getEditText().addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override public void afterTextChanged(Editable s) {
                    searchPosts(s.toString().trim());
                }
            });
        }

        // Phân quyền
        RoleHelper.getCurrentUserRole(role -> {
            if (adapter != null) {
                adapter.setCanEdit(RoleHelper.isAuthorOrAdmin(role));
            }
        });

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new MarketingPostAdapter(filteredPosts);

        // Set click listener
        adapter.setOnItemClickListener(new MarketingPostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MarketingPost post) {
                // Open detail fragment
                openPostDetail(post);
            }

            @Override
            public void onEditClick(MarketingPost post) {
                // Open edit dialog/fragment
                editPost(post);
            }
        });

        binding.rvMarketingPosts.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvMarketingPosts.setAdapter(adapter);
    }

    private void loadAllPosts() {
        FirebaseFirestore.getInstance()
                .collection("marketing_posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        return;
                    }

                    if (snapshot == null) return;

                    allPosts.clear();

                    for (var doc : snapshot.getDocuments()) {
                        MarketingPost post = doc.toObject(MarketingPost.class);
                        if (post != null) {
                            post.setId(doc.getId()); // Set ID từ document
                            allPosts.add(post);
                        }
                    }

                    filteredPosts.clear();
                    filteredPosts.addAll(allPosts);
                    adapter.updateList(filteredPosts);
                    updateStats();
                });
    }

    private void searchPosts(String query) {
        List<MarketingPost> tempList = new ArrayList<>();
        if (query.isEmpty()) {
            tempList.addAll(allPosts);
        } else {
            String q = query.toLowerCase();
            for (MarketingPost p : allPosts) {
                String title = p.getTitle() != null ? p.getTitle().toLowerCase() : "";
                String desc = p.getDescription() != null ? p.getDescription().toLowerCase() : "";

                if (title.contains(q) || desc.contains(q)) {
                    tempList.add(p);
                }
            }
        }
        filteredPosts.clear();
        filteredPosts.addAll(tempList);
        adapter.updateList(filteredPosts);
        updateStats();
    }

    private void updateStats() {
        int total = allPosts.size();
        int active = 0;
        long totalViews = 0;

        for (MarketingPost post : allPosts) {
            if (post.isActive()) {
                active++;
            }
            totalViews += post.getViews();
        }

        // Cập nhật TextView
        binding.tvTotalPosts.setText(String.valueOf(total));
        binding.tvActivePosts.setText(String.valueOf(active));
        binding.tvTotalViews.setText(formatViews(totalViews));
    }

    private String formatViews(long views) {
        if (views >= 1000000) {
            return String.format("%.1fM", views / 1000000.0);
        } else if (views >= 1000) {
            return String.format("%.1fK", views / 1000.0);
        } else {
            return String.valueOf(views);
        }
    }

    private void openPostDetail(MarketingPost post) {
        // Mở fragment chi tiết
        // Cách 1: Truyền ID và load từ Firestore
        MarketingPostDetailFragment fragment = MarketingPostDetailFragment.newInstance(post.getId());
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.navHost, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void editPost(MarketingPost post) {
        // TODO: Mở dialog/fragment để chỉnh sửa
        // Tạm thời hiển thị thông báo
        android.widget.Toast.makeText(requireContext(),
                "Edit post: " + post.getTitle(),
                android.widget.Toast.LENGTH_SHORT).show();
    }
}