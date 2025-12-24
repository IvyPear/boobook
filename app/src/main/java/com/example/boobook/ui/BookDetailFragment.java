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

public class BookDetailFragment extends Fragment {

    private FragmentBookDetailBinding binding;
    private Book book;

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
            bindData();
        }

        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        binding.btnLove.setOnClickListener(v -> {
            binding.btnLove.setIconResource(R.drawable.ic_heart);
            binding.btnLove.setIconTintResource(android.R.color.holo_red_light);
        });

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

        Glide.with(this)
                .load(book.coverUrl)
                .placeholder(R.drawable.book_placeholder)
                .into(binding.ivBlogCover);
    }
}