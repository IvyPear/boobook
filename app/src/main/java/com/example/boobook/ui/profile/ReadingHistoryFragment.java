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
import com.google.firebase.firestore.Query;
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
            // Mở chi tiết sách/truyện
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
        if (uid == null) {
            binding.tvEmpty.setText("Vui lòng đăng nhập để xem lịch sử");
            binding.tvEmpty.setVisibility(View.VISIBLE);
            binding.rvHistory.setVisibility(View.GONE);
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("readHistory")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        binding.tvEmpty.setVisibility(View.VISIBLE);
                        binding.rvHistory.setVisibility(View.GONE);
                        return;
                    }

                    if (snapshot == null || snapshot.isEmpty()) {
                        binding.tvEmpty.setVisibility(View.VISIBLE);
                        binding.rvHistory.setVisibility(View.GONE);
                        return;
                    }

                    historyList.clear();

                    for (var doc : snapshot.getDocuments()) {
                        ContentItem item = new ContentItem();
                        item.id = doc.getId();

                        String type = doc.getString("type");
                        if (type == null) continue;

                        item.type = type;
                        item.title = doc.getString("title");
                        item.author = doc.getString("author");
                        item.coverUrl = doc.getString("coverUrl");

                        // Thêm thời gian đọc vào mô tả
                        if (doc.getTimestamp("readAt") != null) {
                            String time = doc.getTimestamp("readAt").toDate().toString();
                            item.desc = "Đã đọc: " + time.substring(0, Math.min(16, time.length()));
                        } else {
                            item.desc = "Đã đọc gần đây";
                        }

                        historyList.add(item);
                    }

                    adapter.updateList(historyList);
                    binding.tvEmpty.setVisibility(historyList.isEmpty() ? View.VISIBLE : View.GONE);
                    binding.rvHistory.setVisibility(historyList.isEmpty() ? View.GONE : View.VISIBLE);
                });
    }
}