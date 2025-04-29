package com.company.corpconnect.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.company.corpconnect.R;
import com.company.corpconnect.adapter.ImageBannerAdapter;
import com.company.corpconnect.adapter.NewsAdapter;
import com.company.corpconnect.model.News;
import com.company.corpconnect.ui.account.AccountActivity;
import com.company.corpconnect.ui.account.NotificationsActivity;
import com.company.corpconnect.ui.admin.AddNewsActivity;
import com.company.corpconnect.ui.interaction.ChatsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private List<News> newsList = new ArrayList<>();
    private SearchView searchView;
    private DatabaseReference newsRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        recyclerView = findViewById(R.id.newsRecyclerView);
        searchView = findViewById(R.id.searchView);
        FloatingActionButton fabAddNews = findViewById(R.id.fabAddNews);

        setupBanner();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter(newsList, this);
        recyclerView.setAdapter(newsAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://corpconnect-fdf1b-default-rtdb.europe-west1.firebasedatabase.app");
        newsRef = database.getReference("news");
        loadNews();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterNews(newText);
                return true;
            }
        });

        fabAddNews.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, AddNewsActivity.class));
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    return true;
                } else if (itemId == R.id.notification) {
                    startActivity(new Intent(HomeActivity.this, NotificationsActivity.class));
                    return true;
                } else if (itemId == R.id.message) {
                    startActivity(new Intent(HomeActivity.this, ChatsActivity.class));
                    return true;
                } else if (itemId == R.id.account) {
                    startActivity(new Intent(HomeActivity.this, AccountActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
    private void loadNews() {
        newsRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                newsList.clear();
                for (DataSnapshot newsSnapshot : snapshot.getChildren()) {
                    News news = newsSnapshot.getValue(News.class);
                    newsList.add(news);
                }
                newsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void filterNews(String query) {
        List<News> filteredList = new ArrayList<>();
        for (News news : newsList) {
            if (!TextUtils.isEmpty(news.getTitle()) && news.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(news);
            }
        }
        newsAdapter.updateList(filteredList);
    }

    private void setupBanner() {
        ViewPager2 imageBanner = findViewById(R.id.imageBanner);
        List<Integer> images = Arrays.asList(
                R.drawable.banner_image1,
                R.drawable.banner_image2,
                R.drawable.banner_image3
        );

        ImageBannerAdapter adapter = new ImageBannerAdapter(this, images);
        imageBanner.setAdapter(adapter);

        Runnable bannerRunnable = () -> {
            int currentItem = imageBanner.getCurrentItem();
            int nextItem = currentItem + 1 >= images.size() ? 0 : currentItem + 1;
            imageBanner.setCurrentItem(nextItem, true);
        };

        Handler bannerHandler = new Handler();
        imageBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bannerHandler.removeCallbacks(bannerRunnable);
                bannerHandler.postDelayed(bannerRunnable, 3000);
            }
        });
    }

}
