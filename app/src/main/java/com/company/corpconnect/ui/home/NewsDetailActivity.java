package com.company.corpconnect.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.company.corpconnect.R;
import com.company.corpconnect.model.News;
import com.company.corpconnect.ui.account.AccountActivity;
import com.company.corpconnect.ui.account.NotificationsActivity;
import com.company.corpconnect.ui.interaction.ChatsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class NewsDetailActivity extends AppCompatActivity {

    private DatabaseReference userDatabaseRef;
    private String userRole = "user";
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://corpconnect-fdf1b-default-rtdb.europe-west1.firebasedatabase.app");
        userDatabaseRef = database.getReference("users");

        ImageView newsImageDetail = findViewById(R.id.newsImageDetail);
        TextView newsTitleDetail = findViewById(R.id.newsTitleDetail);
        TextView newsAuthorDetail = findViewById(R.id.newsAuthorDetail);
        TextView newsDateDetail = findViewById(R.id.newsDateDetail);
        TextView newsDescriptionDetail = findViewById(R.id.newsDescriptionDetail);
        TextView newsExtraData = findViewById(R.id.newsCategory);
        Button deleteNewsButton = findViewById(R.id.deleteNewsButton);

        News news = (News) getIntent().getSerializableExtra("news");

        if (news != null) {
            newsTitleDetail.setText(news.getTitle());
            newsAuthorDetail.setText(news.getAuthor());
            newsDateDetail.setText(news.getDate());
            newsDescriptionDetail.setText(news.getDescription());
            newsExtraData.setText("Категория: " + "Новости компании");

            Glide.with(this)
                    .load(news.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.ic_app)
                    .into(newsImageDetail);

            deleteNewsButton.setOnClickListener(v -> deleteNews(news.getNewsId()));
        }

        loadUserData(deleteNewsButton);

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

    private void deleteNews(String newsId) {
        DatabaseReference newsRef = FirebaseDatabase.getInstance("https://corpconnect-fdf1b-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("news")
                .child(newsId);

        newsRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Новость успешно удалена", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Ошибка удаления новости", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadUserData(Button deleteNewsButton) {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference userRef = userDatabaseRef.child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userRole = snapshot.child("role").getValue(String.class);
                    if ("admin".equals(userRole)) {
                        deleteNewsButton.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NewsDetailActivity.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
