package com.example.jjsminventoria.database;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import model.Products;

public class FirebaseConnection {
    private static FirebaseConnection instance;
    private final DatabaseReference userDb, productsDb, categoryDb;
    private final StorageReference storageRef;
    private final FirebaseAuth auth;
    private final FirebaseAppCheck firebaseAppCheck;

    private FirebaseConnection() {
        userDb = FirebaseDatabase.getInstance().getReference("Company").child("100").child("Users");
        productsDb = FirebaseDatabase.getInstance().getReference("Company").child("100").child(
                "Products");
        categoryDb =
                FirebaseDatabase.getInstance().getReference("Company").child("100").child(
                        "Categories"); // Add
        // this line

        storageRef = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance());
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

    public void fetchProducts(FetchProductsCallback  callBack) {
        productsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Products> productsList = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Products product = snapshot1.getValue(Products.class);
                    if (product != null) {
                        productsList.add(product);
                    }
                }
                callBack.onProductsFetched(productsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onProductsFetchedFailed(error.toException());
            }
        });
    }

    public void addProductsToCategories(Products product, List<String> categories) {
        String productKey = productsDb.push().getKey();

        if (productKey == null) {
            System.err.println("Failed to generate Firebase key for product.");
            return;
        }

        try {
            product.setId(productKey.hashCode());
            productsDb.child(productKey).setValue(product);

            for (String categoryName : categories) {
                FirebaseDatabase.getInstance().getReference("Company").child("100").child(
                        "Products").child(productKey).setValue(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addCategory(String categoryName) {
        try {
            DatabaseReference categoryRef = categoryDb.child(categoryName);
            categoryRef.child("Products").setValue(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DatabaseReference getCategoryDb() {
        return categoryDb;
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

    public interface FetchProductsCallback{
        void onProductsFetched(List<Products> products);
        void onProductsFetchedFailed(Exception exception);
    }
}
