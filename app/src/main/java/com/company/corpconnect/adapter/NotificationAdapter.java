package com.company.corpconnect.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.corpconnect.R;
import com.company.corpconnect.model.Notification;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<Notification> notifications;

    public NotificationAdapter(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.notificationType.setText(notification.getType());
        holder.notificationText.setText(notification.getText());
        holder.notificationTime.setText(notification.getTime());

        holder.readIndicator.setImageResource(
                notification.isRead() ? R.drawable.ic_read : R.drawable.ic_unread
        );

        holder.itemView.setOnClickListener(v -> {
            if (!notification.isRead()) {
                notification.setRead(true);
                notifyItemChanged(position);
                updateNotificationState(notification);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    private void updateNotificationState(Notification notification) {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance(
                "https://corpconnect-fdf1b-default-rtdb.europe-west1.firebasedatabase.app"
        ).getReference("notifications").child(notification.getId());
        notificationRef.child("isRead").setValue(true);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView notificationType;
        TextView notificationText;
        TextView notificationTime;
        ImageView readIndicator;

        public ViewHolder(View itemView) {
            super(itemView);
            notificationType = itemView.findViewById(R.id.notificationType);
            notificationText = itemView.findViewById(R.id.notificationText);
            notificationTime = itemView.findViewById(R.id.notificationTime);
            readIndicator = itemView.findViewById(R.id.readIndicator);
        }
    }
}
