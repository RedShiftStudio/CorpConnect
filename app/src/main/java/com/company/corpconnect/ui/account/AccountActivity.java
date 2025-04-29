package com.company.corpconnect.ui.account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.company.corpconnect.R;
import com.company.corpconnect.ui.admin.AddNewsActivity;
import com.company.corpconnect.ui.home.HomeActivity;
import com.company.corpconnect.ui.interaction.ChatsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AccountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    startActivity(new Intent(AccountActivity.this, HomeActivity.class));
                    return true;
                } else if (itemId == R.id.notification) {
                    startActivity(new Intent(AccountActivity.this, NotificationsActivity.class));
                    return true;
                } else if (itemId == R.id.message) {
                    startActivity(new Intent(AccountActivity.this, ChatsActivity.class));
                    return true;
                } else if (itemId == R.id.account) {
                    return true;
                }
                return false;
            }
        });
    }
}
