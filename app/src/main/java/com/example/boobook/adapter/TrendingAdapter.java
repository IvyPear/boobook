package com.example.boobook.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.boobook.databinding.ItemTrendingBinding;
import com.example.boobook.model.ContentItem;
import java.util.ArrayList;
import java.util.List;

public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.VH> {

    private List<ContentItem> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ContentItem item);
    }

    public TrendingAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<ContentItem> newList) {
        items.clear();
        items.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTrendingBinding binding = ItemTrendingBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ContentItem item = items.get(position);

        holder.binding.tvTitle.setText(item.title);
        holder.binding.tvAuthor.setText(item.author);

        String badge = item.type != null && item.type.equals("book")
                ? "Sách • " + item.chapterCount + " chương"
                : "Truyện ngắn" + (item.readTime != null ? " • " + item.readTime : "");

        holder.binding.tvAuthor.setText(badge + " • " + item.author); // tạm dùng tvAuthor làm badge

        Glide.with(holder.itemView.getContext())
                .load(item.coverUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_close_clear_cancel)
                .into(holder.binding.ivBookCover);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ItemTrendingBinding binding;
        VH(ItemTrendingBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }
}