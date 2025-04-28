package com.company.corpconnect.ui.login;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.company.corpconnect.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordDialog {

    public static void showForgotPasswordDialog(Context context) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_forgot_password, null);

        final TextInputLayout emailEditText = dialogView.findViewById(R.id.emailEditText);
        Button sendButton = dialogView.findViewById(R.id.sendButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        sendButton.setOnClickListener(v -> {
            String email = emailEditText.getEditText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(context, "Пожалуйста, введите email", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Ссылка для восстановления пароля отправлена на ваш email", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Не удалось отправить ссылку на ваш email" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}