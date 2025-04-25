package com.example.jjsminventoria.database;

import android.text.format.DateFormat;

import androidx.annotation.NonNull;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Products;

public class FirebaseConnection {

    private static FirebaseConnection instance;

    private final DatabaseReference rootDb, usersDb, categoriesDb, historyDb;

    private final StorageReference storageRef;
    private final FirebaseAuth auth;
    private final FirebaseAppCheck firebaseAppCheck;

    private FirebaseConnection() {
        rootDb = FirebaseDatabase.getInstance().getReference("Company").child("100");
        usersDb = rootDb.child("Users");
        categoriesDb = rootDb.child("Categories");
        historyDb = rootDb.child("Users").child("History");

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

    public void fetchProducts(FetchProductsCallback callBack) {

        categoriesDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Products> productsList = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    DataSnapshot productsSnap = snap.child("Products");

                    for (DataSnapshot productSnap : productsSnap.getChildren()) {
                        Products product = parseProduct(productSnap);
                        if (product != null) {
                            productsList.add(product);
                        }
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

    public void fetchProductsUnderCategory(String categoryName, FetchProductsCallback callback) {
        DatabaseReference productsRef = rootDb
                .child("Categories")
                .child(categoryName)
                .child("Products");

        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Products> productList = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    productList.add(parseProduct(snap));
                }
                callback.onProductsFetched(productList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onProductsFetchedFailed(error.toException());
            }
        });
    }

    private Products parseProduct(DataSnapshot snap) {
        Products product = new Products();

        product.setId(snap.getKey());

        Object name = snap.child("name").getValue();
        if (name instanceof String) product.setName((String) name);

        Object desc = snap.child("desc").getValue();
        if (desc instanceof String) product.setDesc((String) desc);

        Object img = snap.child("img").getValue();
        if (img instanceof String) product.setImg((String) img);

        Object qty = snap.child("qty").getValue();
        if (qty instanceof Long) product.setQty(((Long) qty).intValue());

        Object weight = snap.child("weight").getValue();
        if (weight instanceof Double) product.setWeight((Double) weight);
        else if (weight instanceof Long) product.setWeight(((Long) weight).doubleValue());

        Object price = snap.child("price").getValue();
        if (price instanceof Double) product.setPrice((Double) price);
        else if (price instanceof Long) product.setPrice(((Long) price).doubleValue());

        Object discount = snap.child("discount").getValue();
        if (discount instanceof Double) product.setDiscount((Double) discount);
        else if (discount instanceof Long) product.setDiscount(((Long) discount).doubleValue());

        return product;
    }

    public void fetchCategories(FetchCategoriesCallBack callBack) {
        categoriesDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> categories = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    categories.add(snap.getKey());
                }
                callBack.onCategoriesFetched(categories);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onCategoriesFetchedFailed(error.toException());
            }
        });
    }

    public void addProductsToCategories(Products product, List<String> categories) {
        String productKey = rootDb.child("Products").push().getKey();

        if (productKey == null) {
            System.err.println("Failed to generate Firebase key for product.");
            return;
        }

        try {
            product.setId(productKey);

            for (String categoryName : categories) {
                DatabaseReference categoryProductRef = rootDb
                        .child("Categories")
                        .child(categoryName)
                        .child("Products")
                        .child(productKey);

                categoryProductRef.setValue(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addCategory(String categoryName) {
        try {
            DatabaseReference categoryRef = categoriesDb.child(categoryName);
            categoryRef.child("Products").setValue(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logHistory(String actionType, String message) {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "unknown";
        String timestamp = DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date()).toString();

        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("Company")
                .child("100")
                .child("Users")
                .child(userId);

        String historyId = userRef.child("History").push().getKey();
        if (historyId == null) return;

        userRef.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.getValue(String.class);
                if (username == null) username = "Unknown";

                Map<String, Object> historyMap = new HashMap<>();
                historyMap.put("id", historyId);
                historyMap.put("actionType", actionType);
                historyMap.put("message", message);
                historyMap.put("timestamp", timestamp);
                historyMap.put("username", username);

                userRef.child("History").child(historyId).setValue(historyMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    public void logout() {
        auth.signOut();
    }

    // Getters
    public DatabaseReference getRootDb() {
        return rootDb;
    }

    public DatabaseReference getUserDb() {
        return usersDb;
    }

    public DatabaseReference getCategoryDb() {
        return categoriesDb;
    }

    public StorageReference getStorageRef() {
        return storageRef;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    // Callback Interfaces
    public interface FetchProductsCallback {
        void onProductsFetched(List<Products> products);
        void onProductsFetchedFailed(Exception exception);
    }

    public interface FetchCategoriesCallBack {
        void onCategoriesFetched(List<String> categories);
        void onCategoriesFetchedFailed(Exception exception);
    }


    private static String cachedCompanyName;

    public interface CompanyNameCallback {
        void onCompanyNameFetched(String name);
    }

    public void getCompanyNameOnce(CompanyNameCallback callback) {
        if (cachedCompanyName != null) {
            callback.onCompanyNameFetched(cachedCompanyName);
            return;
        }

        rootDb.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cachedCompanyName = snapshot.getValue(String.class);
                callback.onCompanyNameFetched(cachedCompanyName != null ? cachedCompanyName : "Company");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onCompanyNameFetched("Company");
            }
        });
    }


}
