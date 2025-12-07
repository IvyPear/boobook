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
import com.example.boobook.model.MarketingPost;
import java.util.List;

public class MarketingPostAdapter extends RecyclerView.Adapter<MarketingPostAdapter.ViewHolder> {

    private List<MarketingPost> posts;
    private OnItemClickListener listener;
    private boolean canEdit = false;

    public interface OnItemClickListener {
        void onItemClick(MarketingPost post);
        void onEditClick(MarketingPost post);
    }

    public MarketingPostAdapter(List<MarketingPost> posts) {
        this.posts = posts;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public void updateList(List<MarketingPost> newPosts) {
        this.posts = newPosts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_marketing_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MarketingPost post = posts.get(position);

        holder.tvTitle.setText(post.getTitle());
        holder.tvDescription.setText(post.getDescription());
        holder.tvViews.setText(formatViews(post.getViews()));

        if (post.isActive()) {
            holder.tvStatus.setText("Active");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_active);
        } else {
            holder.tvStatus.setText("Inactive");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_inactive);
        }

        // Load image
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(post.getImageUrl())
                    .placeholder(R.drawable.img_marketing_placeholder)
                    .into(holder.ivImage);
        }

        // Show/hide edit button based on permission
        if (canEdit) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEditClick(post);
            });
        } else {
            holder.btnEdit.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(post);
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    private String formatViews(long views) {
        if (views >= 1000000) {
            return String.format("%.1fM views", views / 1000000.0);
        } else if (views >= 1000) {
            return String.format("%.1fK views", views / 1000.0);
        } else {
            return views + " views";
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvDescription, tvViews, tvStatus;
        View btnEdit;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvViews = itemView.findViewById(R.id.tvViews);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}