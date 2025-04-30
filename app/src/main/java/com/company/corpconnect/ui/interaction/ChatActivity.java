package com.company.corpconnect.ui.interaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.corpconnect.R;
import com.company.corpconnect.adapter.ChatAdapter;
import com.company.corpconnect.model.Message;
import com.company.corpconnect.model.Notification;
import com.company.corpconnect.ui.home.HomeActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText messageInput;

    private DatabaseReference chatDatabaseRef;
    private String chatId;
    private String currentUserId;

    private List<Message> messagesList = new ArrayList<>();
    private ChatAdapter chatAdapter;

    @Override
    protected void onStart() {
        super.onStart();
        listenForNotifications();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        ImageButton sendButton = findViewById(R.id.sendButton);

        chatId = getIntent().getStringExtra("CHAT_ID");
        if (chatId == null) {
            Toast.makeText(this, "Ошибка: CHAT_ID не передан", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        currentUserId = getIntent().getStringExtra("USER_ID");

        String chatUserName = getIntent().getStringExtra("CHAT_USER_NAME");

        TextView topBarTitle = findViewById(R.id.topBarTitle);
        if (chatUserName != null) {
            topBarTitle.setText(chatUserName);
        } else {
            fetchUserName(currentUserId);
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance(HomeActivity.linkDatabase);
        chatDatabaseRef = database.getReference("chats").child(chatId);

        chatAdapter = new ChatAdapter(messagesList, currentUserId);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.setItemAnimator(new DefaultItemAnimator());

        loadMessages();

        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString();
            if (!messageText.isEmpty()) {
                sendMessage(chatId, messageText);
            } else {
                Toast.makeText(ChatActivity.this, "Введите сообщение", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void loadMessages() {
        chatDatabaseRef.child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                if (message != null) {
                    messagesList.add(message);
                    chatAdapter.notifyItemInserted(messagesList.size() - 1);
                    chatRecyclerView.scrollToPosition(messagesList.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Можно обработать изменения сообщения, если требуется
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Можно обработать удаление сообщения, если требуется
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Можно обработать перемещение сообщения, если требуется
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Ошибка загрузки сообщений", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage(String chatId, String text) {
        DatabaseReference messagesRef = FirebaseDatabase.getInstance(HomeActivity.linkDatabase)
                .getReference("chats")
                .child(chatId)
                .child("messages");

        String messageId = messagesRef.push().getKey();
        if (messageId == null) {
            Toast.makeText(ChatActivity.this, "Ошибка отправки сообщения", Toast.LENGTH_SHORT).show();
            return;
        }

        Message message = new Message(currentUserId, text, System.currentTimeMillis());

        messagesRef.child(messageId).setValue(message)
                .addOnSuccessListener(aVoid -> {
                    messageInput.setText("");
                    updateChatMetadata(chatId, text);

                    sendNotification(chatId, text);
                })
                .addOnFailureListener(e -> Toast.makeText(ChatActivity.this, "Ошибка отправки сообщения", Toast.LENGTH_SHORT).show());
    }

    private void sendNotification(String chatId, String messageText) {
        DatabaseReference chatMetadataRef = FirebaseDatabase.getInstance(HomeActivity.linkDatabase)
                .getReference("chats")
                .child(chatId)
                .child("metadata");

        chatMetadataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String otherUserId = snapshot.child("user1").getValue(String.class);
                if (otherUserId == null || otherUserId.equals(currentUserId)) {
                    otherUserId = snapshot.child("user2").getValue(String.class);
                }

                if (otherUserId != null) {
                    DatabaseReference notificationsRef = FirebaseDatabase.getInstance(HomeActivity.linkDatabase)
                            .getReference("users")
                            .child(otherUserId)
                            .child("notifications");

                    String notificationId = notificationsRef.push().getKey();
                    if (notificationId != null) {
                        Notification notification = new Notification(notificationId, "Новое сообщение", messageText, String.valueOf(System.currentTimeMillis()), false);
                        notificationsRef.child(notificationId).setValue(notification);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Ошибка создания уведомления", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateChatMetadata(String chatId, String lastMessage) {
        DatabaseReference chatMetadataRef = FirebaseDatabase.getInstance(HomeActivity.linkDatabase)
                .getReference("chats")
                .child(chatId)
                .child("metadata");

        chatMetadataRef.child("lastMessage").setValue(lastMessage);
        chatMetadataRef.child("lastMessageTime").setValue(System.currentTimeMillis());
    }

    private void fetchUserName(String userId) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance(HomeActivity.linkDatabase)
                .getReference("users")
                .child(userId);

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("name")) {
                    String userName = snapshot.child("name").getValue(String.class);
                    String surname = snapshot.child("surname").getValue(String.class);

                    if (userName != null && surname != null) {
                        TextView topBarTitle = findViewById(R.id.topBarTitle);
                        topBarTitle.setText(userName + " " + surname);
                    }
                } else {
                    Toast.makeText(ChatActivity.this, "Имя пользователя не найдено", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Ошибка загрузки данных пользователя", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listenForNotifications() {
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance(HomeActivity.linkDatabase)
                .getReference("users")
                .child(currentUserId)
                .child("notifications");

        notificationsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Notification notification = snapshot.getValue(Notification.class);
                if (notification != null) {
                    Toast.makeText(ChatActivity.this, "Новое уведомление: " + notification.getText(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Обработка изменений в уведомлениях, если нужно
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Обработка удаления уведомлений, если нужно
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Обработка перемещения уведомлений, если нужно
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обработка ошибок, если нужно
            }
        });
    }
}
