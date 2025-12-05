package com.example.boobook.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.boobook.R;
import com.example.boobook.databinding.FragmentBookDetailBinding;
import com.example.boobook.model.Book;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class BookDetailFragment extends Fragment {

    private FragmentBookDetailBinding binding;
    private Book book;
    private String bookId;
    private boolean isLiked = false;
    private DocumentReference bookRef;

    // HỖ TRỢ CẢ 2 CÁCH: NHẬN BOOK HOẶC BOOKID
    public static BookDetailFragment newInstance(Book book) {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("book", book);
        fragment.setArguments(args);
        return fragment;
    }

    public static BookDetailFragment newInstance(String bookId) {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        args.putString("bookId", bookId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBookDetailBinding.inflate(inflater, container, false);

        // LẤY DỮ LIỆU TỪ ARGUMENTS
        if (getArguments() != null) {
            if (getArguments().containsKey("book")) {
                book = (Book) getArguments().getSerializable("book");
                if (book != null) {
                    bookId = book.id;
                    bindData();
                    setupLoveButton();
                }
            } else if (getArguments().containsKey("bookId")) {
                bookId = getArguments().getString("bookId");
                loadBookFromFirestore(); // LOAD DỮ LIỆU TỪ FIRESTORE NẾU CHỈ CÓ ID
            }
        }

        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        binding.btnReadNow.setOnClickListener(v -> {
            if (book != null) {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.navHost, ChaptersFragment.newInstance(book))
                        .addToBackStack("chapters")
                        .commit();
            }
        });

        return binding.getRoot();
    }

    private void loadBookFromFirestore() {
        if (bookId == null || bookId.isEmpty()) return;

        FirebaseFirestore.getInstance()
                .collection("books")
                .document(bookId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        book = snapshot.toObject(Book.class);
                        if (book != null) {
                            book.id = snapshot.getId(); // ĐẢM BẢO CÓ ID
                            bindData();
                            setupLoveButton();
                        }
                    }
                });
    }

    private void bindData() {
        if (book == null) return;

        binding.tvTitle.setText(book.title);
        binding.tvAuthor.setText(book.author);
        binding.tvGenre.setText(book.genre);
        binding.tvViews.setText(book.views + " views");
        binding.tvDescription.setText("Câu chuyện về " + book.title + " – một tác phẩm kinh điển của " + book.author + ".");

        updateLikesCount(book.likes);

        Glide.with(this)
                .load(book.coverUrl)
                .placeholder(R.drawable.book_placeholder)
                .error(android.R.drawable.ic_menu_close_clear_cancel)
                .into(binding.ivBlogCover);
    }

    private void setupLoveButton() {
        if (book == null || book.id == null || book.id.isEmpty()) {
            binding.btnLove.setEnabled(false);
            return;
        }

        bookRef = FirebaseFirestore.getInstance().collection("books").document(book.id);

        // Hiển thị trạng thái hiện tại
        binding.btnLove.setIconResource(book.likes > 0 ? R.drawable.ic_heart_filled : R.drawable.ic_heart);
        binding.btnLove.setIconTintResource(book.likes > 0 ? android.R.color.holo_red_light : android.R.color.white);
        isLiked = book.likes > 0;

        binding.btnLove.setOnClickListener(v -> {
            isLiked = !isLiked;

            if (isLiked) {
                binding.btnLove.setIconResource(R.drawable.ic_heart_filled);
                binding.btnLove.setIconTintResource(android.R.color.holo_red_light);
                bookRef.update("likes", FieldValue.increment(1));
                book.likes++;
            } else {
                binding.btnLove.setIconResource(R.drawable.ic_heart);
                binding.btnLove.setIconTintResource(android.R.color.white);
                bookRef.update("likes", FieldValue.increment(-1));
                book.likes--;
            }
            updateLikesCount(book.likes);
        });
    }

    private void updateLikesCount(long likes) {
        if (likes >= 1000) {
            binding.tvLikes.setText(String.format("%.1fK likes", likes / 1000.0));
        } else {
            binding.tvLikes.setText(likes + " likes");
        }
    }
}