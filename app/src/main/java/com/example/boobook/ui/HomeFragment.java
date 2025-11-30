package com.example.boobook.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.boobook.adapter.BookTrendingAdapter;
import com.example.boobook.databinding.FragmentHomeBinding;
import com.example.boobook.model.Book;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private BookTrendingAdapter trendingAdapter;
    private BookTrendingAdapter newArrivalsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        setupRecyclerViews();
        loadTrendingBooks();
        loadNewArrivals();   // ← ĐÃ CHỈNH ĐỂ LẤY ĐÚNG 5 CUỐN MỚI NHẤT

        return binding.getRoot();
    }

    private void setupRecyclerViews() {
        trendingAdapter = new BookTrendingAdapter();
        newArrivalsAdapter = new BookTrendingAdapter();

        binding.rvTrending.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvTrending.setAdapter(trendingAdapter);

        binding.rvNewArrivals.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvNewArrivals.setAdapter(newArrivalsAdapter);
    }

    private void loadTrendingBooks() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("books")
                .orderBy("views", Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .addOnSuccessListener(snapshot -> trendingAdapter.updateBooks(snapshot.toObjects(Book.class)));
    }

    // ĐÃ CHỈNH CHUẨN: LẤY 5 CUỐN MỚI NHẤT THEO createdAt
    private void loadNewArrivals() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("books")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(5)                                      // ← CHỈ 5 CUỐN
                .get()
                .addOnSuccessListener(snapshot -> {
                    newArrivalsAdapter.updateBooks(snapshot.toObjects(Book.class));
                })
                .addOnFailureListener(e -> {
                    // Nếu chưa có field createdAt → vẫn hiện theo thứ tự document (vẫn ổn)
                });
    }
}