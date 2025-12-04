package com.example.boobook.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.boobook.databinding.ItemBookHorizontalBinding;
import com.example.boobook.model.Book;
import java.util.ArrayList;
import java.util.List;

public class BookHorizontalAdapter extends RecyclerView.Adapter<BookHorizontalAdapter.ViewHolder> {

    private List<Book> books = new ArrayList<>();
    private final OnBookClickListener listener;

    public interface OnBookClickListener {
        void onBookClick(Book book);
    }

    public BookHorizontalAdapter(OnBookClickListener listener) {
        this.listener = listener;
    }

    public void updateBooks(List<Book> books) {
        this.books = books;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBookHorizontalBinding binding = ItemBookHorizontalBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = books.get(position);
        holder.binding.tvTitle.setText(book.title);
        holder.binding.tvAuthor.setText(book.author);

        // ĐÃ SỬA: DÙNG ẢNH MẶC ĐỊNH, KHÔNG CẦN img_book_placeholder NỮA!
        Glide.with(holder.itemView.getContext())
                .load(book.coverUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)  // ảnh mặc định hệ thống
                .error(android.R.drawable.ic_menu_close_clear_cancel) // nếu lỗi
                .into(holder.binding.ivCover);

        holder.itemView.setOnClickListener(v -> listener.onBookClick(book));
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemBookHorizontalBinding binding;

        ViewHolder(ItemBookHorizontalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}