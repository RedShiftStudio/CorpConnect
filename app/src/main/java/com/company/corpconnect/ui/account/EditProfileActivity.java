package com.company.corpconnect.ui.account;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.company.corpconnect.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {
    private TextInputEditText emailEditText, nameEditText, surnameEditText, departmentEditText, positionEditText;
    private DatabaseReference userDatabaseRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        nameEditText = findViewById(R.id.nameEditText);
        surnameEditText = findViewById(R.id.surnameEditText);

        emailEditText = findViewById(R.id.emailEditText);
        positionEditText = findViewById(R.id.positionEditText);
        departmentEditText = findViewById(R.id.departmentEditText);

        Button saveProfileButton = findViewById(R.id.saveProfileButton);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://corpconnect-fdf1b-default-rtdb.europe-west1.firebasedatabase.app");
        userDatabaseRef = database.getReference("users");

        loadUserData();

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        saveProfileButton.setOnClickListener(v -> saveProfileChanges());
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

                    nameEditText.setText(name);
                    surnameEditText.setText(surname);
                    positionEditText.setText(position);
                    departmentEditText.setText(department);
                    emailEditText.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfileActivity.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfileChanges() {
        String name = Objects.requireNonNull(nameEditText.getText()).toString().trim();
        String surname = Objects.requireNonNull(surnameEditText.getText()).toString().trim();
        String position = Objects.requireNonNull(positionEditText.getText()).toString().trim();
        String department = Objects.requireNonNull(departmentEditText.getText()).toString().trim();
        String email = Objects.requireNonNull(emailEditText.getText()).toString().trim();

        if (name.isEmpty()) {
            nameEditText.setError("Введите имя");
            nameEditText.requestFocus();
            return;
        }

        if (surname.isEmpty()) {
            surnameEditText.setError("Введите фамилию");
            surnameEditText.requestFocus();
            return;
        }

        if (position.isEmpty()) {
            positionEditText.setError("Введите должность");
            positionEditText.requestFocus();
            return;
        }

        if (department.isEmpty()) {
            departmentEditText.setError("Введите отдел/подразделение");
            departmentEditText.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            emailEditText.setError("Введите email");
            emailEditText.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Введите корректный email");
            emailEditText.requestFocus();
            return;
        }

        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference userRef = userDatabaseRef.child(userId);

        userRef.child("name").setValue(name);
        userRef.child("surname").setValue(surname);
        userRef.child("position").setValue(position);
        userRef.child("department").setValue(department);
        userRef.child("email").setValue(email);

        Toast.makeText(this, "Данные успешно обновлены", Toast.LENGTH_SHORT).show();
        finish();
    }

}
