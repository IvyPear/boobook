package com.example.boobook.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.boobook.R;
import com.example.boobook.databinding.ItemStoryBinding;  // ← ĐÃ ĐỔI
import com.example.boobook.model.Story;                  // ← ĐÃ ĐỔI
import java.util.ArrayList;
import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {  // ← ĐÃ ĐỔI TÊN CLASS

    private List<Story> stories = new ArrayList<>();
    private final OnStoryClickListener listener;

    public interface OnStoryClickListener { void onStoryClick(Story story); }

    public StoryAdapter(OnStoryClickListener listener) { this.listener = listener; }

    public void updateStories(List<Story> stories) {
        this.stories = stories;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStoryBinding binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    // ... phần đầu giữ nguyên

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Story story = stories.get(position);

        holder.binding.tvTitle.setText(story.title);
        holder.binding.tvDesc.setText(story.desc);
        holder.binding.tvAuthorDate.setText(story.author + " • " + story.getDateString());

        Glide.with(holder.itemView.getContext())
                .load(story.coverUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.binding.ivCover);

        holder.itemView.setOnClickListener(v -> listener.onStoryClick(story));
    }

    @Override public int getItemCount() { return stories.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemStoryBinding binding;
        ViewHolder(ItemStoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}