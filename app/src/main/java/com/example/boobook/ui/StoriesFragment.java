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
import com.example.boobook.adapter.StoryAdapter;
import com.example.boobook.databinding.FragmentStoriesBinding;
import com.example.boobook.model.Story;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class StoriesFragment extends Fragment {

    private FragmentStoriesBinding binding;
    private StoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStoriesBinding.inflate(inflater, container, false);

        // Nút back
        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        // Setup RecyclerView + Adapter
        setupRecyclerView();

        // Load dữ liệu từ Firestore
        loadStoriesFromFirebase();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new StoryAdapter(story -> {
            // Khi click vào 1 story → mở chi tiết
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.navHost, StoryDetailFragment.newInstance(story))
                    .addToBackStack(null)
                    .commit();
        });

        binding.rvStories.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvStories.setAdapter(adapter);
    }

    private void loadStoriesFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("stories")  // ← Đảm bảo tên collection trên Firestore là "stories"
                .orderBy("date", Query.Direction.DESCENDING)  // mới nhất trước
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Story> storyList = new ArrayList<>();
                    for (var doc : queryDocumentSnapshots) {
                        Story story = doc.toObject(Story.class);
                        story.id = doc.getId();  // quan trọng: set ID để sau này dùng
                        storyList.add(story);
                    }
                    adapter.updateStories(storyList);  // ← ĐỔ DỮ LIỆU VÀO ADAPTER
                })
                .addOnFailureListener(e -> {
                    // Có thể thêm Toast nếu muốn
                });
    }
}