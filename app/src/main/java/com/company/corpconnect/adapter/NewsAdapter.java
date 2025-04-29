package com.company.corpconnect.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.company.corpconnect.R;
import com.company.corpconnect.model.News;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<News> newsList;

    public NewsAdapter(List<News> newsList) {
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.NewsViewHolder holder, int position) {
        News news = newsList.get(position);
        holder.title.setText(news.getTitle());
        holder.description.setText(news.getDescription());
        holder.author.setText(news.getAuthor());
        holder.date.setText(news.getDate());

        Glide.with(holder.itemView.getContext())
                .load(news.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.ic_app)
                .into(holder.newsImage);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<News> newNewsList) {
        this.newsList = new ArrayList<>(newNewsList);
        notifyDataSetChanged();
    }
    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, author, date;
        ImageView newsImage;
        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.newsTitle);
            description = itemView.findViewById(R.id.newsDescription);
            author = itemView.findViewById(R.id.newsAuthor);
            date = itemView.findViewById(R.id.newsDate);
            newsImage = itemView.findViewById(R.id.newsImage);

        }
    }
}
