package com.company.corpconnect.ui.registration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.company.corpconnect.R;
import com.company.corpconnect.model.User;
import com.company.corpconnect.ui.home.HomeActivity;
import com.company.corpconnect.ui.login.LoginActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText emailEditText, passwordEditText, nameEditText, surnameEditText, departmentEditText, positionEditText;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        nameEditText = findViewById(R.id.nameEditText);
        surnameEditText = findViewById(R.id.surnameEditText);

        emailEditText = findViewById(R.id.emailEditText);
        positionEditText = findViewById(R.id.positionEditText);
        departmentEditText = findViewById(R.id.departmentEditText);

        passwordEditText = findViewById(R.id.passwordEditText);
        Button registerButton = findViewById(R.id.registerButton);
        TextView loginLink = findViewById(R.id.loginLink);

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance(HomeActivity.linkDatabase);
        databaseReference = database.getReference("users");
        registerButton.setOnClickListener(v -> {
            String name = Objects.requireNonNull(nameEditText.getText()).toString().trim();
            String surname = Objects.requireNonNull(surnameEditText.getText()).toString().trim();

            String email = Objects.requireNonNull(emailEditText.getText()).toString().trim();
            String password = Objects.requireNonNull(passwordEditText.getText()).toString().trim();
            String position = Objects.requireNonNull(positionEditText.getText()).toString().trim();
            String department = Objects.requireNonNull(departmentEditText.getText()).toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Введите имя", Toast.LENGTH_SHORT).show();
                return;
            }

            if (surname.isEmpty()) {
                Toast.makeText(this, "Введите фамилию", Toast.LENGTH_SHORT).show();
                return;
            }

            if (email.isEmpty() || password.isEmpty() || position.isEmpty() || department.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Некорректный email");
                return;
            }
            if (password.length() < 6) {
                passwordEditText.setError("Минимум 6 символов");
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                            saveUserToDatabase(userId, email, name, position, surname, department);
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Неизвестная ошибка";
                            Toast.makeText(this, "Ошибка: " + error, Toast.LENGTH_LONG).show();
                        }
                    });
        });

        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void saveUserToDatabase(String userId, String email, String name, String position, String surname, String department) {
        User user = new User(userId, email, name, surname, "user", position, department);

        databaseReference.child(userId).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            } else {
                String error = task.getException() != null ? task.getException().getMessage() : "Ошибка сохранения данных";
                Log.e("RegisterActivity", "Ошибка при записи в базу: " + error);
                Toast.makeText(this, "Ошибка при записи в базу: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}