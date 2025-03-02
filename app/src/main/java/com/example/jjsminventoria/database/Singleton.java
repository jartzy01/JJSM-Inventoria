package com.example.jjsminventoria.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Singleton {
    private static Singleton instance;
    private final DatabaseReference userDb;

    private Singleton() {
        userDb = FirebaseDatabase.getInstance().getReference("Users");
    }

    public static synchronized Singleton getInstance(){
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }

    public DatabaseReference getUserDb() {
        return userDb;
    }
}
