package com.example.jjsminventoria.database;

import android.text.format.DateFormat;

import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirebaseConnection {
    private static FirebaseConnection instance;
    private final DatabaseReference userDb;
    private final DatabaseReference categoryDb;
    private final DatabaseReference historyDb;
    private final StorageReference storageRef;
    private final FirebaseAuth auth;
    private final FirebaseAppCheck firebaseAppCheck;
    private final DatabaseReference itemDb;


    private FirebaseConnection() {
        userDb = FirebaseDatabase.getInstance().getReference("Users");
        categoryDb = FirebaseDatabase.getInstance().getReference("Categories");
        historyDb = FirebaseDatabase.getInstance().getReference("History");
        itemDb = FirebaseDatabase.getInstance().getReference("Items");
        storageRef = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance());
    }

    public static FirebaseConnection getInstance() {
        if (instance == null) {
            synchronized (FirebaseConnection.class) {
                if (instance == null) {
                    instance = new FirebaseConnection();
                }
            }
        }
        return instance;
    }

    public DatabaseReference getCategoryDb() {
        return categoryDb;
    }

    public DatabaseReference getUserDb() {
        return userDb;
    }

    public DatabaseReference getHistoryDb() {
        return historyDb;
    }

    public StorageReference getStorageRef() {
        return storageRef;
    }

    public DatabaseReference getItemDb() {
        return itemDb;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public void logout() {
        auth.signOut();
    }


    public void logHistory(String actionType, String message) {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "unknown";
        String timestamp = DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date()).toString();
        String historyId = historyDb.push().getKey();

        if (historyId == null) return;

        Map<String, Object> historyMap = new HashMap<>();
        historyMap.put("id", historyId);
        historyMap.put("actionType", actionType);
        historyMap.put("message", message);
        historyMap.put("timestamp", timestamp);

        historyDb.child(userId).child(historyId).setValue(historyMap);
    }
}
