package com.example.boobook.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.boobook.R;
import com.example.boobook.adapter.AllContentAdapter;
import com.example.boobook.databinding.FragmentFavoritesBinding;
import com.example.boobook.model.ContentItem;
import com.example.boobook.model.Story;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private FragmentFavoritesBinding binding;
    private AllContentAdapter adapter;
    private final List<ContentItem> favoriteList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);

        Log.d("FAVORITES", "FavoritesFragment created");

        setupRecyclerView();
        loadFavorites();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new AllContentAdapter(item -> {
            Log.d("FAVORITES", "Item clicked: " + item.title);
            if ("book".equals(item.type)) {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.navHost, BookDetailFragment.newInstance(item.id))
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            } else {
                Story story = new Story();
                story.id = item.id;
                story.title = item.title;
                story.author = item.author;
                story.coverUrl = item.coverUrl;
                story.content = item.desc;
                story.readTime = item.readTime;
                story.date = item.date;

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.navHost, StoryDetailFragment.newInstance(story))
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        binding.rvFavorites.setLayoutManager(layoutManager);
        binding.rvFavorites.setAdapter(adapter);
    }

    private void loadFavorites() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            showEmpty(true);
            binding.tvEmpty.setText("Vui lòng đăng nhập để xem yêu thích");
            return;
        }

        String userId = currentUser.getUid();
        Log.d("FAVORITES", "Loading favorites for user: " + userId);

        // LOAD TẤT CẢ FAVORITES - SẼ CÓ FIELD "type" để phân biệt
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("favorites")
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null || snapshot == null) {
                        showEmpty(true);
                        return;
                    }

                    favoriteList.clear();

                    // XỬ LÝ MỖI FAVORITE DOCUMENT
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        ContentItem item = new ContentItem();
                        item.id = doc.getId();
                        item.type = doc.getString("type");  // "book" hoặc "story"
                        item.title = doc.getString("title");
                        item.author = "";  // Có thể lưu thêm author trong favorites
                        item.coverUrl = doc.getString("coverUrl");
                        item.desc = "Đã thêm vào yêu thích";  // Mô tả mặc định

                        favoriteList.add(item);
                        Log.d("FAVORITES", "Added favorite: " + item.title + " (" + item.type + ")");
                    }

                    if (favoriteList.isEmpty()) {
                        showEmpty(true);
                    } else {
                        adapter.updateList(favoriteList);
                        showEmpty(false);
                        Log.d("FAVORITES", "Total favorites: " + favoriteList.size());
                    }
                });
    }

    private void showEmpty(boolean empty) {
        if (empty) {
            binding.tvEmpty.setVisibility(View.VISIBLE);
            binding.rvFavorites.setVisibility(View.GONE);
        } else {
            binding.tvEmpty.setVisibility(View.GONE);
            binding.rvFavorites.setVisibility(View.VISIBLE);
        }
    }
}