package com.example.jjsminventoria.database;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseConnection {
    private static FirebaseConnection instance;
    private final DatabaseReference userDb;
    private final StorageReference storageRef;
    private final FirebaseAuth auth;

    private FirebaseConnection() {
        userDb = FirebaseDatabase.getInstance().getReference("Users");
        storageRef = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
    }

    public static FirebaseConnection getInstance(){
        if (instance == null) {
            synchronized (FirebaseConnection.class) {
                if (instance == null) {
                    instance = new FirebaseConnection();
                }
            }
        }
        return instance;
    }

    public void logout() {
        auth.signOut();
    }

    public DatabaseReference getUserDb() {
        return userDb;
    }

    public StorageReference getStorageRef() {
        return storageRef;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }
}
