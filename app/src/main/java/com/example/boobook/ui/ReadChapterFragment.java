package com.example.boobook.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.example.boobook.databinding.FragmentReadChapterBinding;
import com.example.boobook.model.Book;
import com.example.boobook.model.Chapter;
import com.example.boobook.utils.HistoryHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ReadChapterFragment extends Fragment {

    private FragmentReadChapterBinding binding;
    private Book book;
    private Chapter currentChapter;
    private int currentChapterIndex = 0;
    private java.util.List<Chapter> allChapters = new java.util.ArrayList<>();

    public static ReadChapterFragment newInstance(Book book, Chapter chapter) {
        ReadChapterFragment f = new ReadChapterFragment();
        Bundle args = new Bundle();
        args.putSerializable("book", book);
        args.putSerializable("chapter", chapter);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReadChapterBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            book = (Book) getArguments().getSerializable("book");
            currentChapter = (Chapter) getArguments().getSerializable("chapter");
            loadAllChaptersAndDisplayCurrent();

            // LƯU LỊCH SỬ ĐỌC SÁCH KHI BẮT ĐẦU ĐỌC
            if (book != null && book.id != null) {
                HistoryHelper.saveBookHistory(
                        book.id,
                        book.title != null ? book.title : "Không có tiêu đề",
                        book.coverUrl != null ? book.coverUrl : "",
                        book.author != null ? book.author : "Không rõ tác giả"
                );
            }
        }

        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        binding.btnPrevious.setOnClickListener(v -> loadPreviousChapter());
        binding.btnNext.setOnClickListener(v -> loadNextChapter());

        return binding.getRoot();
    }

    private void loadAllChaptersAndDisplayCurrent() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("books").document(book.id)
                .collection("chapters")
                .orderBy("chapterNumber", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    allChapters = snapshot.toObjects(Chapter.class);
                    // Tìm index của chapter hiện tại
                    for (int i = 0; i < allChapters.size(); i++) {
                        if (allChapters.get(i).title.equals(currentChapter.title)) {
                            currentChapterIndex = i;
                            break;
                        }
                    }
                    displayChapter(allChapters.get(currentChapterIndex));
                    updateButtons();
                });
    }

    private void displayChapter(Chapter chapter) {
        binding.toolbar.setTitle(chapter.title);
        binding.tvChapterTitle.setText(chapter.title);
        binding.tvContent.setText(chapter.content);
    }

    private void loadPreviousChapter() {
        if (currentChapterIndex > 0) {
            currentChapterIndex--;
            displayChapter(allChapters.get(currentChapterIndex));
            updateButtons();
        }
    }

    private void loadNextChapter() {
        if (currentChapterIndex < allChapters.size() - 1) {
            currentChapterIndex++;
            displayChapter(allChapters.get(currentChapterIndex));
            updateButtons();
        }
    }

    private void updateButtons() {
        binding.btnPrevious.setEnabled(currentChapterIndex > 0);
        binding.btnNext.setEnabled(currentChapterIndex < allChapters.size() - 1);
    }
}