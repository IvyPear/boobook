package com.example.boobook.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.boobook.R;
import com.example.boobook.databinding.FragmentBookDetailBinding;
import com.example.boobook.model.Book;
import com.example.boobook.utils.FavoriteHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class BookDetailFragment extends Fragment {

    private FragmentBookDetailBinding binding;
    private Book book;
    private String bookId;
    private boolean isFavorite = false;  // ĐỔI TÊN TỪ isLiked -> isFavorite
    private FirebaseUser currentUser;

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

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // LẤY DỮ LIỆU TỪ ARGUMENTS
        if (getArguments() != null) {
            if (getArguments().containsKey("book")) {
                book = (Book) getArguments().getSerializable("book");
                if (book != null) {
                    bookId = book.id;
                    bindData();
                    setupFavoriteButton();  // ĐỔI TÊN TỪ setupLoveButton
                }
            } else if (getArguments().containsKey("bookId")) {
                bookId = getArguments().getString("bookId");
                loadBookFromFirestore();
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
                            book.id = snapshot.getId();
                            bindData();
                            setupFavoriteButton();
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

    private void setupFavoriteButton() {
        if (book == null || book.id == null || book.id.isEmpty()) {
            binding.btnLove.setEnabled(false);
            return;
        }

        // KIỂM TRA ĐĂNG NHẬP
        if (currentUser == null) {
            binding.btnLove.setEnabled(false);
            return;
        }

        // KIỂM TRA TRẠNG THÁI FAVORITE HIỆN TẠI
        checkFavoriteStatus();

        binding.btnLove.setOnClickListener(v -> {
            toggleFavorite();
        });
    }

    private void checkFavoriteStatus() {
        if (currentUser == null || book == null || book.id == null) {
            updateFavoriteIcon(false);
            return;
        }

        DocumentReference favRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.getUid())
                .collection("favorites")
                .document(book.id);

        favRef.get().addOnSuccessListener(snapshot -> {
            if (isAdded()) {
                isFavorite = snapshot.exists();
                updateFavoriteIcon(isFavorite);
            }
        }).addOnFailureListener(e -> {
            if (isAdded()) updateFavoriteIcon(false);
        });
    }

    private void toggleFavorite() {
        if (currentUser == null) {
            showToast("Vui lòng đăng nhập để sử dụng tính năng này");
            return;
        }

        if (book == null || book.id == null || !isAdded()) return;

        // DÙNG FavoriteHelper ĐỂ THÊM/XÓA FAVORITE
        FavoriteHelper.toggle(
                book.id,
                "book",
                book.title,
                book.coverUrl,
                isNowFavorite -> {
                    if (isAdded()) {
                        isFavorite = isNowFavorite;
                        updateFavoriteIcon(isFavorite);
                        showToast(isNowFavorite ? "Đã thêm vào yêu thích" : "Đã xóa khỏi yêu thích");

                        // ĐỒNG THỜI CẬP NHẬT LIKES COUNT (tùy chọn)
                        if (isNowFavorite) {
                            updateBookLikes(1);
                        } else {
                            updateBookLikes(-1);
                        }
                    }
                }
        );
    }

    private void updateBookLikes(int increment) {
        DocumentReference bookRef = FirebaseFirestore.getInstance()
                .collection("books")
                .document(book.id);

        bookRef.update("likes", FieldValue.increment(increment))
                .addOnSuccessListener(aVoid -> {
                    book.likes += increment;
                    updateLikesCount(book.likes);
                });
    }

    private void updateFavoriteIcon(boolean isFavorite) {
        binding.btnLove.setIconResource(isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart);
        binding.btnLove.setIconTintResource(isFavorite ?
                android.R.color.holo_red_light : android.R.color.white);
    }

    private void updateLikesCount(long likes) {
        if (likes >= 1000) {
            binding.tvLikes.setText(String.format("%.1fK likes", likes / 1000.0));
        } else {
            binding.tvLikes.setText(likes + " likes");
        }
    }

    private void showToast(String message) {
        if (isAdded() && getContext() != null) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}