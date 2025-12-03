package com.example.boobook.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.boobook.R;
import com.example.boobook.adapter.ChapterAdapter;
import com.example.boobook.databinding.FragmentChaptersBinding;
import com.example.boobook.model.Book;
import com.example.boobook.model.Chapter;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class ChaptersFragment extends Fragment {

    private FragmentChaptersBinding binding;
    private ChapterAdapter adapter;
    private Book book;

    public static ChaptersFragment newInstance(Book book) {
        ChaptersFragment f = new ChaptersFragment();
        Bundle args = new Bundle();
        args.putSerializable("book", book);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChaptersBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            book = (Book) getArguments().getSerializable("book");
            setupRecyclerView();
            loadChapters();
        }

        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new ChapterAdapter(chapter -> {
            // Mở đọc chapter (sẽ làm sau)
            ReadChapterFragment fragment = ReadChapterFragment.newInstance(book, chapter);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.navHost, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        binding.rvChapters.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvChapters.setAdapter(adapter);
    }

    private void loadChapters() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("books").document(book.id)
                .collection("chapters")
                .orderBy("chapterNumber")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Chapter> chapters = snapshot.toObjects(Chapter.class);
                    adapter.updateChapters(chapters);
                });
    }
}