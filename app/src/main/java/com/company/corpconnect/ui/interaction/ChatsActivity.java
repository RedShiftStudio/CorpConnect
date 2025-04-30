package com.company.corpconnect.ui.interaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.corpconnect.R;
import com.company.corpconnect.adapter.UserSearchAdapter;
import com.company.corpconnect.model.User;
import com.company.corpconnect.ui.account.AccountActivity;
import com.company.corpconnect.ui.account.NotificationsActivity;
import com.company.corpconnect.ui.account.UserProfileActivity;
import com.company.corpconnect.ui.home.HomeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatsActivity extends AppCompatActivity {

    private UserSearchAdapter usersAdapter;
    private List<User> userList = new ArrayList<>();

    private DatabaseReference usersRef;
    private String currentUserId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        RecyclerView recyclerView = findViewById(R.id.messagesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        usersAdapter = new UserSearchAdapter(userList, user -> {
            Intent intent = new Intent(ChatsActivity.this, UserProfileActivity.class);
            intent.putExtra("USER_ID", user.getId());
            startActivity(intent);
        });

        recyclerView.setAdapter(usersAdapter);

        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://corpconnect-fdf1b-default-rtdb.europe-west1.firebasedatabase.app");
        usersRef = database.getReference("users");

        setupSearchView();
        setupBottomNavigation();
        loadUsers();
    }

    private void setupSearchView() {
        SearchView searchView = findViewById(R.id.item_user_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUsers(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchUsers(newText);
                return false;
            }
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    startActivity(new Intent(ChatsActivity.this, HomeActivity.class));
                    return true;
                } else if (itemId == R.id.notification) {
                    startActivity(new Intent(ChatsActivity.this, NotificationsActivity.class));
                    return true;
                } else if (itemId == R.id.message) {
                    return true;
                } else if (itemId == R.id.account) {
                    startActivity(new Intent(ChatsActivity.this, AccountActivity.class));
                    return true;
                }
                return false;
            }
        });
    }

    private void searchUsers(String query) {
        if (query.isEmpty()) {
            loadUsers();
        } else {
            Query searchQuery = usersRef.orderByChild("name").startAt(query).endAt(query + "\uf8ff");
            searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("ChatsActivity", "DataSnapshot: " + snapshot);
                    userList.clear();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        User user = data.getValue(User.class);
                        if (user != null && !user.getId().equals(currentUserId)) {
                            userList.add(user);
                            Log.d("ChatsActivity", "Added user: " + user.getName());
                        }
                    }
                    usersAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ChatsActivity.this, "Ошибка поиска", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadUsers() {

        usersRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null && !user.getId().equals(currentUserId)) {
                        userList.add(user);
                    }
                }
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatsActivity.this, "Ошибка загрузки пользователей", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
