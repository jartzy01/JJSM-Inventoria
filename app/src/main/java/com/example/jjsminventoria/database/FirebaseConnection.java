package com.example.jjsminventoria.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseConnection {
    private static FirebaseConnection instance;
    private final DatabaseReference userDb;

    private FirebaseConnection() {
        userDb = FirebaseDatabase.getInstance().getReference("Users");
    }

    public static synchronized FirebaseConnection getInstance(){
        if (instance == null) {
            instance = new FirebaseConnection();
        }
        return instance;
    }

    public DatabaseReference getUserDb() {
        return userDb;
    }
}
