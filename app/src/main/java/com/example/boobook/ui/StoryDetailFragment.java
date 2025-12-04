package com.example.boobook.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.boobook.R;
import com.example.boobook.databinding.FragmentStoryDetailBinding;
import com.example.boobook.model.Story;

public class StoryDetailFragment extends Fragment {

    private FragmentStoryDetailBinding binding;
    private Story story;

    public static StoryDetailFragment newInstance(Story story) {
        StoryDetailFragment f = new StoryDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("story", story);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStoryDetailBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            story = (Story) getArguments().getSerializable("story");
            bindData();
        }

        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        return binding.getRoot();
    }

    private void bindData() {
        binding.tvTitle.setText(story.title);
        binding.tvAuthor.setText(story.author);
        binding.tvAuthorInitials.setText(getInitials(story.author));

        String dateTime = story.getDateString();
        if (story.readTime != null && !story.readTime.isEmpty()) {
            dateTime += " • " + story.readTime;
        }
        binding.tvDateReadTime.setText(dateTime);

        binding.tvContent.setText(story.content);

        // ĐÃ SỬA: LOẠI BỎ HOÀN TOÀN FILE HỎNG, DÙNG ẢNH HỆ THỐNG
        Glide.with(this)
                .load(story.coverUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_close_clear_cancel)
                .into(binding.ivBlogCover);
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isEmpty()) sb.append(Character.toUpperCase(p.charAt(0)));
            if (sb.length() >= 2) break;
        }
        return sb.toString();
    }
}