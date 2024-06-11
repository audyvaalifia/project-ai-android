package com.example.projekakhirai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_AI = 2;

    private List<ChatMessage> chatMessages;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).isUser()) {
            return VIEW_TYPE_USER;
        } else {
            return VIEW_TYPE_AI;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_USER) {
            View view = inflater.inflate(R.layout.item_chat_message_user, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_chat_message_ai, parent, false);
            return new AIViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_USER) {
            ((UserViewHolder) holder).bind(message);
        } else {
            ((AIViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView iconImageView;

        UserViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
        }

        void bind(ChatMessage message) {
            messageTextView.setText(message.getText());
            iconImageView.setImageResource(R.drawable.ic_person);
        }
    }

    static class AIViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView iconImageView;

        AIViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
        }

        void bind(ChatMessage message) {
            messageTextView.setText(message.getText());
            iconImageView.setImageResource(R.drawable.ic_giyuu);
        }
    }
}
