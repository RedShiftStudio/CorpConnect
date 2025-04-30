package com.company.corpconnect.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.corpconnect.R;
import com.company.corpconnect.model.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_OUTGOING = 1;
    private static final int VIEW_TYPE_INCOMING = 2;

    private List<Message> messagesList;
    private String currentUserId;

    public ChatAdapter(List<Message> messagesList, String currentUserId) {
        this.messagesList = messagesList;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messagesList.get(position);
        if (message.getSenderId().equals(currentUserId)) {
            return VIEW_TYPE_OUTGOING;
        } else {
            return VIEW_TYPE_INCOMING;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_OUTGOING) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_outgoing, parent, false);
            return new OutgoingMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_incoming, parent, false);
            return new IncomingMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messagesList.get(position);

        if (holder instanceof OutgoingMessageViewHolder) {
            ((OutgoingMessageViewHolder) holder).bind(message);
        } else if (holder instanceof IncomingMessageViewHolder) {
            ((IncomingMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    static class OutgoingMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, messageTime;

        OutgoingMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            messageTime = itemView.findViewById(R.id.message_time);
        }

        void bind(Message message) {
            messageText.setText(message.getText());
            messageTime.setText(formatTime(message.getTimestamp()));
        }
    }

    static class IncomingMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, messageTime;

        IncomingMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            messageTime = itemView.findViewById(R.id.message_time);
        }

        void bind(Message message) {
            messageText.setText(message.getText());
            messageTime.setText(formatTime(message.getTimestamp()));
        }
    }

    private static String formatTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}

