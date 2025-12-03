package com.example.boobook.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.boobook.R;
import com.example.boobook.databinding.FragmentBookDetailBinding;
import com.example.boobook.model.Book;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class BookDetailFragment extends Fragment {

    private FragmentBookDetailBinding binding;
    private Book book;
    private boolean isLiked = false;
    private DocumentReference bookRef;

    public static BookDetailFragment newInstance(Book book) {
        BookDetailFragment f = new BookDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("book", book);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBookDetailBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            book = (Book) getArguments().getSerializable("book");
            if (book != null) {
                bindData();
                setupLoveButton();
            }
        }

        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        binding.btnReadNow.setOnClickListener(v -> {
            // Sắp tới mở phần đọc truyện
        });

        return binding.getRoot();
    }

    private void bindData() {
        binding.tvTitle.setText(book.title);
        binding.tvAuthor.setText(book.author);
        binding.tvGenre.setText(book.genre);
        binding.tvViews.setText(book.views + " views");
        binding.tvDescription.setText("Câu chuyện về " + book.title + " – một tác phẩm kinh điển của " + book.author + ".");

        updateLikesCount(book.likes);

        Glide.with(this)
                .load(book.coverUrl)
                .placeholder(R.drawable.book_placeholder)
                .into(binding.ivBlogCover);
    }

    private void setupLoveButton() {
        if (book.id == null || book.id.isEmpty()) {
            binding.btnLove.setEnabled(false);
            return;
        }

        bookRef = FirebaseFirestore.getInstance().collection("books").document(book.id);

        // Hiển thị trạng thái ban đầu
        if (book.likes > 0) {
            binding.btnLove.setIconResource(R.drawable.ic_heart_filled); // cần có icon này
            binding.btnLove.setIconTintResource(android.R.color.holo_red_light);
            isLiked = true;
        } else {
            binding.btnLove.setIconResource(R.drawable.ic_heart);
            binding.btnLove.setIconTintResource(android.R.color.white);
        }

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