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

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<Notification> notifications;

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
        holder.type.setText(notification.getType());
        holder.text.setText(notification.getText());
        holder.time.setText(notification.getTime());
        holder.readIndicator.setImageResource(notification.isRead() ? R.drawable.ic_read : R.drawable.ic_unread);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView type, text, time;
        ImageView readIndicator;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.notificationType);
            text = itemView.findViewById(R.id.notificationText);
            time = itemView.findViewById(R.id.notificationTime);
            readIndicator = itemView.findViewById(R.id.readIndicator);
        }
    }
}
