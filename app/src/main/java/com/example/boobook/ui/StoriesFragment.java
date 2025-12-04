package com.example.boobook.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.boobook.R;
import com.example.boobook.adapter.AllContentAdapter;
import com.example.boobook.databinding.FragmentStoriesBinding;
import com.example.boobook.model.ContentItem;
import com.example.boobook.model.Story;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class StoriesFragment extends Fragment {

    private FragmentStoriesBinding binding;
    private AllContentAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStoriesBinding.inflate(inflater, container, false);

        // Nút back (nếu có toolbar)
        if (binding.toolbar != null) {
            binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        }

        setupRecyclerView();
        loadAllBooksAndStories(); // TẢI CẢ SÁCH + TRUYỆN NGẮN

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new AllContentAdapter(item -> {
            if ("book".equals(item.type)) {
                // TODO: Sau này mở BookDetailFragment
                // Hiện tại chỉ Toast để test
                android.widget.Toast.makeText(requireContext(), "Mở sách: " + item.title, android.widget.Toast.LENGTH_SHORT).show();
            } else {
                // Chuyển ContentItem → Story để mở StoryDetail (vẫn dùng được bình thường)
                Story story = new Story();
                story.id = item.id;
                story.title = item.title;
                story.author = item.author;
                story.coverUrl = item.coverUrl;
                story.content = item.desc;        // hoặc item.content nếu có
                story.readTime = item.readTime;
                story.date = item.date;

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.navHost, StoryDetailFragment.newInstance(story))
                        .addToBackStack(null)
                        .commit();
            }
        });

        binding.rvStories.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvStories.setAdapter(adapter);
    }

    // TẢI CẢ SÁCH + TRUYỆN NGẮN VÀO 1 LIST DUY NHẤT
    private void loadAllBooksAndStories() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<ContentItem> allContent = new ArrayList<>();

        // 1. Lấy hết truyện ngắn
        db.collection("stories")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(storySnapshot -> {
                    for (DocumentSnapshot doc : storySnapshot) {
                        ContentItem item = doc.toObject(ContentItem.class);
                        if (item != null) {
                            item.id = doc.getId();
                            item.type = "story";
                            allContent.add(item);
                        }
                    }

                    // 2. Lấy hết sách
                    db.collection("books")
                            .get()
                            .addOnSuccessListener(bookSnapshot -> {
                                for (DocumentSnapshot doc : bookSnapshot) {
                                    ContentItem item = new ContentItem();
                                    item.id = doc.getId();
                                    item.type = "book";
                                    item.title = doc.getString("title");
                                    item.author = doc.getString("author");
                                    item.coverUrl = doc.getString("coverUrl");
                                    item.desc = doc.getString("desc");
                                    item.views = doc.getLong("views") != null ? doc.getLong("views") : 0L;
                                    item.likes = doc.getLong("likes") != null ? doc.getLong("likes") : 0L;
                                    item.chapterCount = doc.getLong("chapterCount") != null ? doc.getLong("chapterCount") : 1L;
                                    allContent.add(item);
                                }

                                // Sắp xếp: mới nhất trước
                                allContent.sort((a, b) -> b.getDateString().compareTo(a.getDateString()));

                                adapter.updateList(allContent);
                            });
                })
                .addOnFailureListener(e -> {
                    // Có thể thêm Snackbar/Toast lỗi mạng
                });
    }
}