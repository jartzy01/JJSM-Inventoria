package com.example.jjsminventoria.ui.settings;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jjsminventoria.database.FirebaseConnection;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import model.Users;

public class SettingsViewModel extends ViewModel {
    private final MutableLiveData<Users> usersInfo = new MutableLiveData<>();

    public SettingsViewModel() {
        fetchUser();
    }

    private void fetchUser() {
        FirebaseUser currentUser = FirebaseConnection.getInstance().getAuth().getCurrentUser();
        if (currentUser == null) return;

        DatabaseReference userRef = FirebaseConnection.getInstance().getUserDb();

        userRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                usersInfo.setValue(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public LiveData<Users> getUserInfo() {
        return usersInfo;
    }
}