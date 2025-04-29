package com.company.corpconnect.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.company.corpconnect.R;
import com.company.corpconnect.model.News;
import com.company.corpconnect.ui.account.AccountActivity;
import com.company.corpconnect.ui.account.NotificationsActivity;
import com.company.corpconnect.ui.interaction.ChatsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NewsDetailActivity extends AppCompatActivity {

    private ImageView newsImageDetail;
    private TextView newsTitleDetail, newsAuthorDetail, newsDateDetail, newsDescriptionDetail, newsExtraData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        newsImageDetail = findViewById(R.id.newsImageDetail);
        newsTitleDetail = findViewById(R.id.newsTitleDetail);
        newsAuthorDetail = findViewById(R.id.newsAuthorDetail);
        newsDateDetail = findViewById(R.id.newsDateDetail);
        newsDescriptionDetail = findViewById(R.id.newsDescriptionDetail);
        newsExtraData = findViewById(R.id.newsCategory);

        News news = (News) getIntent().getSerializableExtra("news");

        if (news != null) {
            newsTitleDetail.setText(news.getTitle());
            newsAuthorDetail.setText(news.getAuthor());
            newsDateDetail.setText(news.getDate());
            newsDescriptionDetail.setText(news.getDescription());
            newsExtraData.setText("Категория: " + "Новости компании"); // Дополнительная информация

            Glide.with(this)
                    .load(news.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.ic_app)
                    .into(newsImageDetail);
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    startActivity(new Intent(NewsDetailActivity.this, HomeActivity.class));
                    return true;
                } else if (itemId == R.id.notification) {
                    startActivity(new Intent(NewsDetailActivity.this, NotificationsActivity.class));
                    return true;
                } else if (itemId == R.id.message) {
                    startActivity(new Intent(NewsDetailActivity.this, ChatsActivity.class));
                    return true;
                } else if (itemId == R.id.account) {
                    startActivity(new Intent(NewsDetailActivity.this, AccountActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
}
