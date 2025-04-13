package com.example.jjsminventoria.adpters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jjsminventoria.R;

import java.util.List;

import model.Users;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UsersViewHolder> {
    private List<Users> usersList;

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        LinearLayout actionContainer;

        public UsersViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.tvRVUsername);
            actionContainer = itemView.findViewById(R.id.usersActionContainer);
        }
    }

    public UserAdapter(List<Users> usersList) {
        this.usersList = usersList;
    }

    public void updateUsers(List<Users> newUsers) {
        this.usersList = newUsers;
        notifyDataSetChanged(); // Refresh the adapter
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_list, parent, false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        Users user = usersList.get(position);
        holder.userName.setText(user.getName());
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

}
