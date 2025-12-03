package com.example.boobook.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.boobook.R;
import com.example.boobook.model.Book;
import com.example.boobook.ui.BookDetailFragment;
import androidx.fragment.app.FragmentActivity;
import java.util.ArrayList;
import java.util.List;

public class BookTrendingAdapter extends RecyclerView.Adapter<BookTrendingAdapter.ViewHolder> {

    private List<Book> bookList = new ArrayList<>();
    private final FragmentActivity activity;

    public BookTrendingAdapter(FragmentActivity activity) {
        this.activity = activity;
    }

    public void updateBooks(List<Book> books) {
        bookList.clear();
        bookList.addAll(books);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trending, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = bookList.get(position);

        holder.tvTitle.setText(book.title);
        holder.tvAuthor.setText(book.author);

        Glide.with(holder.itemView.getContext())
                .load(book.coverUrl)
                .placeholder(R.drawable.book_placeholder)
                .error(R.drawable.book_placeholder)
                .into(holder.ivBookCover);

        // CLICK MỞ DETAIL – DÙNG ĐÚNG ID navHost CỦA BẠN
        holder.itemView.setOnClickListener(v -> {
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.navHost, BookDetailFragment.newInstance(book))  // ← ĐÚNG ID: navHost
                    .addToBackStack("detail")
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBookCover;
        TextView tvTitle, tvAuthor;

        ViewHolder(View itemView) {
            super(itemView);
            ivBookCover = itemView.findViewById(R.id.ivBookCover);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
        }
    }
}