package com.company.corpconnect.ui.admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.company.corpconnect.R;
import com.company.corpconnect.model.News;
import com.company.corpconnect.ui.home.HomeActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddNewsActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etAuthor;
    private ImageView ivImagePreview;
    private Uri imageUri;
    private DatabaseReference newsRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_news);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etAuthor = findViewById(R.id.etAuthor);
        ivImagePreview = findViewById(R.id.ivImagePreview);
        Button btnAddNews = findViewById(R.id.btnAddNews);
        Button btnChooseImage = findViewById(R.id.btnChooseImage);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://corpconnect-fdf1b-default-rtdb.europe-west1.firebasedatabase.app");
        newsRef = database.getReference("news");

        btnChooseImage.setOnClickListener(v -> openFileChooser());
        btnAddNews.setOnClickListener(v -> addNews());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Выберите изображение"), 71);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 71 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Log.d("AddNewsActivity", "Выбрано изображение: " + imageUri);

            if (imageUri != null) {
                try {
                    Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    Bitmap scaledBitmap = scaleBitmap(originalBitmap);
                    ivImagePreview.setImageBitmap(scaledBitmap);
                    Log.d("AddNewsActivity", "Изображение успешно установлено");
                } catch (IOException e) {
                    Log.e("AddNewsActivity", "Ошибка загрузки изображения: " + e.getMessage(), e);
                }
            } else {
                Log.e("AddNewsActivity", "URI изображения отсутствует");
            }
        } else {
            Log.e("AddNewsActivity", "Выбор изображения отменен или произошла ошибка");
        }
    }

    private Bitmap scaleBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width > 1024 || height > 1024) {
            float ratio = Math.min(
                    (float) 1024 / width,
                    (float) 1024 / height
            );
            width = Math.round(ratio * width);
            height = Math.round(ratio * height);
            return Bitmap.createScaledBitmap(bitmap, width, height, true);
        }
        return bitmap;
    }

    private void addNews() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(author)) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            new Thread(() -> {
                try {
                    Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    Bitmap scaledBitmap = scaleBitmap(originalBitmap);

                    String fileName = System.currentTimeMillis() + ".jpg";
                    String imagePath = saveImageToInternalStorage(scaledBitmap, fileName);

                    if (imagePath != null) {
                        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                        runOnUiThread(() -> addNewsToDatabase(title, description, author, imagePath, date));
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Не удалось сохранить изображение локально", Toast.LENGTH_SHORT).show());
                    }
                } catch (IOException e) {
                    Log.e("AddNewsActivity", "Ошибка обработки изображения: " + e.getMessage(), e);
                }
            }).start();
        } else {
            Toast.makeText(this, "Изображение не выбрано", Toast.LENGTH_SHORT).show();
        }
    }

    private String saveImageToInternalStorage(Bitmap bitmap, String fileName) {
        File directory = new File(getFilesDir(), "local_images");
        if (!directory.exists()) {
            directory.mkdir();
        }

        File file = new File(directory, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
            Log.d("AddNewsActivity", "Изображение сохранено локально: " + file.getAbsolutePath());
            return file.getAbsolutePath();
        } catch (IOException e) {
            Log.e("AddNewsActivity", "Ошибка сохранения изображения: " + e.getMessage(), e);
            return null;
        }
    }

    private void addNewsToDatabase(String title, String description, String author, String imagePath, String date) {
        String newsId = newsRef.push().getKey();

        if (newsId != null) {
            News news = new News(newsId, title, description, author, imagePath, date);

            newsRef.child(newsId).setValue(news)
                    .addOnSuccessListener(aVoid -> {
                        startActivity(new Intent(AddNewsActivity.this, HomeActivity.class));
                        Toast.makeText(this, "Новость успешно добавлена", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("AddNewsActivity", "Ошибка добавления новости: " + e.getMessage(), e);
                    });
        } else {
            Toast.makeText(this, "Ошибка генерации ID новости", Toast.LENGTH_SHORT).show();
        }
    }
}
