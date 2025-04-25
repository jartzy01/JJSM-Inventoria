package com.example.jjsminventoria.adpters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jjsminventoria.R;

import java.util.List;

import model.HistoryItem;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<HistoryItem> historyList;

    public HistoryAdapter(List<HistoryItem> historyList) {
        this.historyList = historyList;
    }

    public void setHistoryList(List<HistoryItem> newList) {
        this.historyList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryItem item = historyList.get(position);

        holder.message.setText(item.getMessage());
        holder.timestamp.setText(item.getTimestamp());
        holder.username.setText(item.getUsername() != null ? item.getUsername() : "Unknown");

        String type = item.getActionType();

        if (type == null) {
            holder.icon.setImageResource(R.drawable.folder_green);
            return;
        }

        switch (type.trim()) {
            case "CREATE":
            case "Create Category":
                holder.icon.setImageResource(R.drawable.folder_green);
                break;

            case "Delete":
            case "Delete Category":
                holder.icon.setImageResource(R.drawable.folder_red);
                break;

            case "Create Item":
                holder.icon.setImageResource(R.drawable.item_green);
                break;

            case "Delete Item":
                holder.icon.setImageResource(R.drawable.item_red);
                break;

            case "Modify Item":
                holder.icon.setImageResource(R.drawable.item);
                break;

            default:
                holder.icon.setImageResource(R.drawable.folder_green); // fallback
                break;
        }
    }


    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView message, timestamp, username;
        ImageView icon;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.historyIcon);
            message = itemView.findViewById(R.id.historyMessage);
            timestamp = itemView.findViewById(R.id.historyTimestamp);
            username = itemView.findViewById(R.id.historyUsername);
        }
    }
}
