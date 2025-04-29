package com.company.corpconnect.ui.account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.corpconnect.R;
import com.company.corpconnect.adapter.NotificationAdapter;
import com.company.corpconnect.model.Notification;
import com.company.corpconnect.ui.home.HomeActivity;
import com.company.corpconnect.ui.interaction.ChatsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notifications = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nofications);

        recyclerView = findViewById(R.id.notificationsRecyclerView);
        Button markAllReadButton = findViewById(R.id.markAllReadButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(notifications);
        recyclerView.setAdapter(adapter);

        // Загрузка уведомлений
        loadNotifications();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    startActivity(new Intent(NotificationsActivity.this, HomeActivity.class));
                    return true;
                } else if (itemId == R.id.notification) {
                    return true;
                } else if (itemId == R.id.message) {
                    startActivity(new Intent(NotificationsActivity.this, ChatsActivity.class));
                    return true;
                } else if (itemId == R.id.account) {
                    startActivity(new Intent(NotificationsActivity.this, AccountActivity.class));
                    return true;
                }
                return false;
            }
        });
    }

    private void loadNotifications() {
        notifications.add(new Notification("Новость", "Это пример уведомления", "10:30", false));
        notifications.add(new Notification("Сообщение", "Сообщение от пользователя", "10:45", true));
        adapter.notifyDataSetChanged();
    }
}
