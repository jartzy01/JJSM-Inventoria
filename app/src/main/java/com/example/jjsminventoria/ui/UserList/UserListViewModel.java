package com.example.jjsminventoria.ui.UserList;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jjsminventoria.database.FirebaseConnection;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import model.Users;

public class UserListViewModel extends ViewModel {

    private final MutableLiveData<List<Users>> usersLiveData = new MutableLiveData<>();
    private final List<Users> usersList = new ArrayList<>();

    public UserListViewModel() {
        loadUsersFromFirebase();
    }

    private void loadUsersFromFirebase() {
        DatabaseReference usersRef = FirebaseConnection.getInstance().getUserDb();

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    // Log the key of each child node
                    Log.d("UserKey", "Key: " + userSnap.getKey());

                    if (!userSnap.getKey().equals("History")) {
                        Users users = userSnap.getValue(Users.class);
                        if (users != null) {
                            usersList.add(users);
                        } else {
                            Log.d("UserListViewModel", "User data is null for: " + userSnap.getKey());
                        }
                    } else {
                        Log.d("UserListViewModel", "Skipping History node: " + userSnap.getKey());
                    }
                }
                usersLiveData.setValue(usersList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to load users: " + error.getMessage(), error.toException());
            }
        });
    }

    public LiveData<List<Users>> getUsers() {
        return usersLiveData;
    }

    // TODO: Implement the ViewModel
}