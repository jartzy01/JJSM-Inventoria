package com.example.jjsminventoria.ui.UserList;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.jjsminventoria.R;
import com.example.jjsminventoria.adpters.UserAdapter;

import java.util.List;

import model.Users;

public class UserListFragment extends Fragment {

    private UserListViewModel mViewModel;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<Users> usersList;

    private ImageButton ibBack, ibPersonCheckMark, ibShare;
    private TextView tvAdd;

    public static UserListFragment newInstance() {
        return new UserListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        ibBack = view.findViewById(R.id.ibBack);
        ibPersonCheckMark = view.findViewById(R.id.ibPersonCheckMark);
        ibShare = view.findViewById(R.id.ibShare);
        tvAdd = view.findViewById(R.id.tvAdd);

        ibBack.setOnClickListener(v->{
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        recyclerView = view.findViewById(R.id.rvUserList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(UserListViewModel.class);

        mViewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            usersList = users;
            if (userAdapter == null) {
                userAdapter = new UserAdapter(users);
                recyclerView.setAdapter(userAdapter);
            } else {
                userAdapter.updateUsers(usersList);
            }
        });
    }

}