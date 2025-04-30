package com.company.corpconnect.ui.account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.company.corpconnect.R;
import com.company.corpconnect.ui.home.HomeActivity;
import com.company.corpconnect.ui.interaction.ChatActivity;
import com.company.corpconnect.ui.interaction.ChatsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity {

    private TextView userName, userPosition, userDepartment, userEmail;
    private DatabaseReference userDatabaseRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userName = findViewById(R.id.userName);
        userPosition = findViewById(R.id.userPosition);
        userDepartment = findViewById(R.id.userDepartment);
        userEmail = findViewById(R.id.userEmail);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://corpconnect-fdf1b-default-rtdb.europe-west1.firebasedatabase.app");
        userDatabaseRef = database.getReference("users");

        loadUserData();
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        Button textProfileButton = findViewById(R.id.textProfileButton);
        textProfileButton.setOnClickListener(v -> {
            String userId = getIntent().getStringExtra("USER_ID");

            if (userId == null) {
                Toast.makeText(UserProfileActivity.this, "Ошибка: ID пользователя не передан", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

            String chatId = currentUserId.compareTo(userId) < 0
                    ? currentUserId + "_" + userId
                    : userId + "_" + currentUserId;

            Intent intent = new Intent(UserProfileActivity.this, ChatActivity.class);
            intent.putExtra("USER_ID", userId);
            intent.putExtra("CHAT_ID", chatId);
            startActivity(intent);
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    startActivity(new Intent(UserProfileActivity.this, HomeActivity.class));
                    return true;
                } else if (itemId == R.id.notification) {
                    startActivity(new Intent(UserProfileActivity.this, NotificationsActivity.class));
                    return true;
                } else if (itemId == R.id.message) {
                    startActivity(new Intent(UserProfileActivity.this, ChatsActivity.class));
                    return true;
                } else if (itemId == R.id.account) {
                    startActivity(new Intent(UserProfileActivity.this, AccountActivity.class));
                    return true;
                }
                return false;
            }
        });
    }

    private void loadUserData() {
        String userId = getIntent().getStringExtra("USER_ID");

        if (userId == null) {
            Toast.makeText(UserProfileActivity.this, "Ошибка: ID пользователя не передан", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userDatabaseRef.child(userId).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String surname = snapshot.child("surname").getValue(String.class);
                    String position = snapshot.child("position").getValue(String.class);
                    String department = snapshot.child("department").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);

                    userName.setText(name + " " + surname);
                    userPosition.setText(position);
                    userDepartment.setText(department);
                    userEmail.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
