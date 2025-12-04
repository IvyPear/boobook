package com.example.boobook.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.boobook.databinding.ItemStoryBinding;
import com.example.boobook.model.ContentItem;
import java.util.ArrayList;
import java.util.List;

public class AllContentAdapter extends RecyclerView.Adapter<AllContentAdapter.VH> {

    private List<ContentItem> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClick(ContentItem item);
    }

    public AllContentAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateList(List<ContentItem> newList) {
        items.clear();
        items.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStoryBinding binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ContentItem item = items.get(position);

        holder.binding.tvTitle.setText(item.title);
        holder.binding.tvDesc.setText(item.desc != null ? item.desc : "Đang cập nhật...");
        holder.binding.tvAuthorDate.setText(item.author + " • " + item.getDateString());

        String badge = item.type.equals("book") ? "Sách • " + item.chapterCount + " chương"
                : "Truyện ngắn" + (item.readTime != null ? " • " + item.readTime : "");
        holder.binding.tvAuthorDate.setText(badge + " • " + item.author);

        Glide.with(holder.itemView.getContext())
                .load(item.coverUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.binding.ivCover);

        holder.itemView.setOnClickListener(v -> listener.onClick(item));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ItemStoryBinding binding;
        VH(ItemStoryBinding b) { super(b.getRoot()); binding = b; }
    }
}