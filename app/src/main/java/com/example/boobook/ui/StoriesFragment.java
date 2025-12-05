package com.example.boobook.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoriesFragment extends Fragment {

    private FragmentStoriesBinding binding;
    private AllContentAdapter adapter;
    private final List<ContentItem> masterList = new ArrayList<>();
    private String currentCategory = "all";
    private String currentQuery = "";

    // Danh sách thể loại + "Khác"
    private final String[] categories = {
            "all", "adventure", "horror", "mystery", "scifi", "classic",
            "fairytale", "folklore", "fantasy", "satire", "danmei",
            "children", "romance", "comedy", "historical",
            "psychology", "biography", "mythology", "other"
    };

    private final String[] categoryNames = {
            "Tất cả", "Phiêu lưu", "Kinh dị", "Bí ẩn", "Khoa học viễn tưởng", "Cổ điển",
            "Cổ tích", "Dân gian", "Kỳ ảo", "Châm biếm", "Đam mỹ",
            "Thiếu nhi", "Lãng mạn", "Hài hước", "Lịch sử",
            "Tâm lý", "Tiểu sử", "Thần thoại", "Khác"
    };

    // Ánh xạ tiếng Anh → code chuẩn (dù viết hoa, có dấu phẩy, khoảng trắng)
    private static final Map<String, String> ENGLISH_TO_CODE = new HashMap<>();
    static {
        ENGLISH_TO_CODE.put("adventure", "adventure");
        ENGLISH_TO_CODE.put("horror", "horror");
        ENGLISH_TO_CODE.put("mystery", "mystery");
        ENGLISH_TO_CODE.put("scifi", "scifi");
        ENGLISH_TO_CODE.put("sci-fi", "scifi");
        ENGLISH_TO_CODE.put("sciencefiction", "scifi");
        ENGLISH_TO_CODE.put("classic", "classic");
        ENGLISH_TO_CODE.put("fairytale", "fairytale");
        ENGLISH_TO_CODE.put("fairy tale", "fairytale");
        ENGLISH_TO_CODE.put("folklore", "folklore");
        ENGLISH_TO_CODE.put("folk", "folklore");
        ENGLISH_TO_CODE.put("fantasy", "fantasy");
        ENGLISH_TO_CODE.put("satire", "satire");
        ENGLISH_TO_CODE.put("danmei", "danmei");
        ENGLISH_TO_CODE.put("children", "children");
        ENGLISH_TO_CODE.put("childrens", "children");
        ENGLISH_TO_CODE.put("romance", "romance");
        ENGLISH_TO_CODE.put("comedy", "comedy");
        ENGLISH_TO_CODE.put("historical", "historical");
        ENGLISH_TO_CODE.put("history", "historical");
        ENGLISH_TO_CODE.put("psychology", "psychology");
        ENGLISH_TO_CODE.put("biography", "biography");
        ENGLISH_TO_CODE.put("mythology", "mythology");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStoriesBinding.inflate(inflater, container, false);
        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        setupTabs();
        setupSearch();
        setupRecyclerView();
        loadAllContentSafely();

        return binding.getRoot();
    }

    private void setupTabs() {
        binding.tabCategory.removeAllTabs();
        for (String name : categoryNames) {
            binding.tabCategory.addTab(binding.tabCategory.newTab().setText(name));
        }
        binding.tabCategory.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                currentCategory = categories[tab.getPosition()];
                applyFilterAndSearch();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {
                binding.rvStories.smoothScrollToPosition(0);
            }
        });
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                currentQuery = s.toString().trim().toLowerCase();
                applyFilterAndSearch();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new AllContentAdapter(item -> {
            if (!isAdded() || getActivity() == null) return;

            if ("book".equals(item.type)) {
                // MỞ TRANG CHI TIẾT SÁCH – TRUYỀN CHỈ ID (String) → GIỐNG HỆT BÊN HOME!!!
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.navHost, BookDetailFragment.newInstance(item.id))
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            } else {
                // Truyện ngắn → mở bình thường
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

        binding.rvStories.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvStories.setAdapter(adapter);
    }

    // LẤY DỮ LIỆU AN TOÀN + HỖ TRỢ FIELD "genre" + NHIỀU THỂ LOẠI
    private void loadAllContentSafely() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("stories").get().addOnSuccessListener(snap -> {
            for (DocumentSnapshot doc : snap) {
                ContentItem item = new ContentItem();
                item.id = doc.getId();
                item.type = "story";
                item.title = doc.getString("title");
                item.author = doc.getString("author");
                item.coverUrl = doc.getString("coverUrl");
                item.desc = doc.getString("content");
                item.readTime = doc.getString("readTime");
                item.date = doc.get("date");
                item.views = doc.getLong("views");
                item.genreList = getGenresFromDocument(doc); // SIÊU QUAN TRỌNG
                if (item.title != null) masterList.add(item);
            }

            db.collection("books").get().addOnSuccessListener(snap2 -> {
                for (DocumentSnapshot doc : snap2) {
                    ContentItem item = new ContentItem();
                    item.id = doc.getId();
                    item.type = "book";
                    item.title = doc.getString("title");
                    item.author = doc.getString("author");
                    item.coverUrl = doc.getString("coverUrl");
                    item.desc = doc.getString("desc");
                    item.chapterCount = doc.getLong("chapterCount") != null ? doc.getLong("chapterCount") : 1L;
                    item.views = doc.getLong("views");
                    item.genreList = getGenresFromDocument(doc);
                    if (item.title != null) masterList.add(item);
                }
                applyFilterAndSearch();
            });
        });
    }

    // HÀM SIÊU THÔNG MINH: XỬ LÝ "Horror, Folk, Mystery"
    private String[] getGenresFromDocument(DocumentSnapshot doc) {
        String genreStr = doc.getString("genre");
        if (genreStr == null || genreStr.trim().isEmpty()) {
            return new String[]{"other"};
        }

        String[] parts = genreStr.toLowerCase()
                .replace(" ", "")
                .replace("-", "")
                .split(",");

        List<String> result = new ArrayList<>();
        for (String part : parts) {
            String cleaned = part.trim();
            if (cleaned.isEmpty()) continue;
            String code = ENGLISH_TO_CODE.getOrDefault(cleaned, "other");
            if (!result.contains(code)) result.add(code);
        }
        return result.isEmpty() ? new String[]{"other"} : result.toArray(new String[0]);
    }

    // LỌC HOÀN HẢO VỚI NHIỀU THỂ LOẠI
    private void applyFilterAndSearch() {
        List<ContentItem> filtered = new ArrayList<>();

        for (ContentItem item : masterList) {
            boolean matchCategory = currentCategory.equals("all");
            if (!matchCategory && item.genreList != null) {
                for (String g : item.genreList) {
                    if (g.equals(currentCategory)) {
                        matchCategory = true;
                        break;
                    }
                }
            }

            boolean matchSearch = currentQuery.isEmpty() ||
                    (item.title != null && item.title.toLowerCase().contains(currentQuery)) ||
                    (item.author != null && item.author.toLowerCase().contains(currentQuery));

            if (matchCategory && matchSearch) {
                filtered.add(item);
            }
        }
        adapter.updateList(filtered);
    }
}