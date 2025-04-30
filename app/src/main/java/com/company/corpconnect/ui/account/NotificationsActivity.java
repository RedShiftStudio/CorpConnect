package com.company.corpconnect.ui.account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        markAllReadButton.setOnClickListener(v -> markAllNotificationsAsRead());

        setupBottomNavigation();
        loadNotificationsFromFirebase();
    }

    private void setupBottomNavigation() {
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

    private void loadNotificationsFromFirebase() {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance(HomeActivity.linkDatabase).getReference("notifications");

        notificationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notifications.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Notification notification = data.getValue(Notification.class);
                    if (notification != null) {
                        notifications.add(notification);
                    }
                }
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обработка ошибок (логирование или отображение сообщения)
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateUI() {
        if (notifications.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void markAllNotificationsAsRead() {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance(HomeActivity.linkDatabase).getReference("notifications");

        Map<String, Object> updates = new HashMap<>();
        for (Notification notification : notifications) {
            if (!notification.isRead()) {
                notification.setRead(true);
                updates.put(notification.getId() + "/isRead", true);
            }
        }

        notificationRef.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                adapter.notifyDataSetChanged();
            } else {
            }
        });
    }
}
