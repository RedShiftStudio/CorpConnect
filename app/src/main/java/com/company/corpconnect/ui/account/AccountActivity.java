package com.company.corpconnect.ui.account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.company.corpconnect.ui.interaction.ChatsActivity;
import com.company.corpconnect.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AccountActivity extends AppCompatActivity {
    private TextView userName, userPosition, userDepartment, userEmail;
    private DatabaseReference userDatabaseRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        userName = findViewById(R.id.userName);
        userPosition = findViewById(R.id.userPosition);
        userDepartment = findViewById(R.id.userDepartment);
        userEmail = findViewById(R.id.userEmail);


        FirebaseDatabase database = FirebaseDatabase.getInstance(HomeActivity.linkDatabase);
        userDatabaseRef = database.getReference("users");

        loadUserData();

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        Button editProfileButton = findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(v -> openEditProfileActivity());

        Button exitProfileButton = findViewById(R.id.exitProfileButton);
        exitProfileButton.setOnClickListener(v -> logout());

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
                } else return itemId == R.id.account;
            }
        });
    }

    private void loadUserData() {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference userRef = userDatabaseRef.child(userId);

        userRef.addValueEventListener(new ValueEventListener() {
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
                Toast.makeText(AccountActivity.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        clearUserData();
        Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void clearUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private void openEditProfileActivity() {
        Intent intent = new Intent(AccountActivity.this, EditProfileActivity.class);
        startActivity(intent);
    }
}
