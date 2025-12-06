package com.example.boobook.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.boobook.R;
import com.example.boobook.adapter.AllContentAdapter;
import com.example.boobook.databinding.FragmentReadingHistoryBinding;
import com.example.boobook.model.ContentItem;
import com.example.boobook.model.Story;
import com.example.boobook.ui.BookDetailFragment;
import com.example.boobook.ui.StoryDetailFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class ReadingHistoryFragment extends Fragment {

    private FragmentReadingHistoryBinding binding;
    private AllContentAdapter adapter;
    private final List<ContentItem> historyList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReadingHistoryBinding.inflate(inflater, container, false);

        setupRecyclerView();
        loadReadingHistory();

        binding.ivBack.setOnClickListener(v -> requireActivity().onBackPressed());

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new AllContentAdapter(item -> {
            // Mở chi tiết sách/truyện như Favorites
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

        binding.rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvHistory.setAdapter(adapter);
    }

    private void loadReadingHistory() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (uid == null) return;

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("readHistory")
                .addSnapshotListener((snapshot, error) -> {
                    if (snapshot == null || snapshot.isEmpty()) {
                        binding.tvEmpty.setVisibility(View.VISIBLE);
                        binding.rvHistory.setVisibility(View.GONE);
                        return;
                    }

                    List<String> bookIds = new ArrayList<>();
                    for (var doc : snapshot) {
                        bookIds.add(doc.getId());
                    }

                    historyList.clear();
                    loadBooksFromHistory(bookIds);
                    loadStoriesFromHistory(bookIds);
                });
    }

    private void loadBooksFromHistory(List<String> ids) {
        FirebaseFirestore.getInstance().collection("books")
                .get()
                .addOnSuccessListener(snapshot -> {
                    for (var doc : snapshot) {
                        if (ids.contains(doc.getId())) {
                            ContentItem item = new ContentItem();
                            item.id = doc.getId();
                            item.type = "book";
                            item.title = doc.getString("title");
                            item.author = doc.getString("author");
                            item.coverUrl = doc.getString("coverUrl");
                            item.desc = doc.getString("desc");
                            item.chapterCount = doc.getLong("chapterCount");
                            historyList.add(item);
                        }
                    }
                    updateList();
                });
    }

    private void loadStoriesFromHistory(List<String> ids) {
        FirebaseFirestore.getInstance().collection("stories")
                .get()
                .addOnSuccessListener(snapshot -> {
                    for (var doc : snapshot) {
                        if (ids.contains(doc.getId())) {
                            ContentItem item = new ContentItem();
                            item.id = doc.getId();
                            item.type = "story";
                            item.title = doc.getString("title");
                            item.author = doc.getString("author");
                            item.coverUrl = doc.getString("coverUrl");
                            item.desc = doc.getString("content");
                            item.readTime = doc.getString("readTime");
                            historyList.add(item);
                        }
                    }
                    updateList();
                });
    }

    private int loaded = 0;
    private void updateList() {
        loaded++;
        if (loaded >= 2) {
            adapter.updateList(historyList);
            binding.tvEmpty.setVisibility(historyList.isEmpty() ? View.VISIBLE : View.GONE);
            binding.rvHistory.setVisibility(historyList.isEmpty() ? View.GONE : View.VISIBLE);
            loaded = 0;
        }
    }
}