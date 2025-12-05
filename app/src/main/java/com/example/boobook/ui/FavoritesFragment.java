package com.example.boobook.ui;

import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
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

        setupRecyclerView();
        loadFavorites();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new AllContentAdapter(item -> {
            if ("book".equals(item.type)) {
                // TODO: Mở Book Detail
                showToast("Sắp mở sách: " + item.title);
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
                        .commit();
            }
        });

        binding.rvFavorites.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvFavorites.setAdapter(adapter);
    }

    private void loadFavorites() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userId).collection("favorites")
                .addSnapshotListener((snap, e) -> {
                    if (e != null || snap == null) {
                        binding.tvEmpty.setVisibility(View.VISIBLE);
                        return;
                    }

                    favoriteList.clear();
                    List<String> favoriteIds = new ArrayList<>();
                    for (var doc : snap) {
                        favoriteIds.add(doc.getId());
                    }

                    if (favoriteIds.isEmpty()) {
                        showEmptyState(true);
                        return;
                    }

                    // Lấy dữ liệu từ stories
                    db.collection("stories").whereIn("id", favoriteIds).get()
                            .addOnSuccessListener(stories -> addToList(stories, "story"));

                    // Lấy dữ liệu từ books
                    db.collection("books").whereIn("id", favoriteIds).get()
                            .addOnSuccessListener(books -> addToList(books, "book"));
                });
    }

    private void addToList(QuerySnapshot snap, String type) {
        for (var doc : snap) {
            ContentItem item = new ContentItem();
            item.id = doc.getId();
            item.type = type;
            item.title = doc.getString("title");
            item.author = doc.getString("author");
            item.coverUrl = doc.getString("coverUrl");
            item.desc = type.equals("story") ? doc.getString("content") : doc.getString("desc");
            item.readTime = doc.getString("readTime");
            item.date = doc.get("date");
            item.chapterCount = doc.getLong("chapterCount");
            favoriteList.add(item);
        }

        adapter.updateList(favoriteList);
        showEmptyState(favoriteList.isEmpty());
    }

    private void showEmptyState(boolean empty) {
        binding.tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.rvFavorites.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void showToast(String msg) {
        android.widget.Toast.makeText(requireContext(), msg, android.widget.Toast.LENGTH_SHORT).show();
    }
}