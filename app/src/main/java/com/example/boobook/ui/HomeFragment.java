package com.example.boobook.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.boobook.R;
import com.example.boobook.adapter.BookTrendingAdapter;
import com.example.boobook.adapter.StoryAdapter;
import com.example.boobook.databinding.FragmentHomeBinding;
import com.example.boobook.model.Book;
import com.example.boobook.model.Story;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private BookTrendingAdapter trendingAdapter;
    private BookTrendingAdapter newArrivalsAdapter;
    private StoryAdapter storyAdapter; // ← THÊM MỚI

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        setupRecyclerViews();
        loadTrendingBooks();
        loadNewArrivals();
        loadLatestStories(); // ← THÊM MỚI

        return binding.getRoot();
    }

    private void setupRecyclerViews() {
        trendingAdapter = new BookTrendingAdapter(requireActivity());
        newArrivalsAdapter = new BookTrendingAdapter(requireActivity());

        binding.rvTrending.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvTrending.setAdapter(trendingAdapter);

        binding.rvNewArrivals.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvNewArrivals.setAdapter(newArrivalsAdapter);

        // Setup Stories
        storyAdapter = new StoryAdapter(story -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.navHost, StoryDetailFragment.newInstance(story))
                    .addToBackStack(null)
                    .commit();
        });
        binding.rvLatestStories.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvLatestStories.setAdapter(storyAdapter);

        // Nút See All Stories
        binding.tvSeeAllStories.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.navHost, new StoriesFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void loadTrendingBooks() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("books")
                .orderBy("views", Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Book> books = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Book book = document.toObject(Book.class);
                        book.id = document.getId();
                        book.likes = document.getLong("likes") != null ? document.getLong("likes") : 0;
                        books.add(book);
                    }
                    trendingAdapter.updateBooks(books);
                });
    }

    private void loadNewArrivals() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("books")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Book> books = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Book book = document.toObject(Book.class);
                        book.id = document.getId();
                        book.likes = document.getLong("likes") != null ? document.getLong("likes") : 0;
                        books.add(book);
                    }
                    newArrivalsAdapter.updateBooks(books);
                });
    }

    // THÊM MỚI – LOAD STORIES
    private void loadLatestStories() {
        FirebaseFirestore.getInstance()
                .collection("stories")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Story> list = new ArrayList<>();
                    for (var doc : snapshot) {
                        Story s = doc.toObject(Story.class);
                        s.id = doc.getId();
                        list.add(s);
                    }
                    storyAdapter.updateStories(list);
                });
    }
}