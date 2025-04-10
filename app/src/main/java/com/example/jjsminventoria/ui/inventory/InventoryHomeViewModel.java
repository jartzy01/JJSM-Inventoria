package com.example.jjsminventoria.ui.inventory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jjsminventoria.database.FirebaseConnection;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import model.Products;

public class InventoryHomeViewModel extends ViewModel {

    private List<Products> allProducts = new ArrayList<>();
    private final MutableLiveData<List<Products>> filteredProducts = new MutableLiveData<>();
    private final MutableLiveData<String> mText;

    public InventoryHomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Inventory fragment");

        loadProductsFromFirebase();
    }

    public LiveData<List<Products>> getFilteredProducts() {
        return filteredProducts;
    }

    public LiveData<String> getText() {
        return mText;
    }

    private void loadProductsFromFirebase() {
        FirebaseConnection.getInstance().fetchProducts(new FirebaseConnection.FetchProductsCallback() {
            @Override
            public void onProductsFetched(List<Products> products) {
                filteredProducts.setValue(products);
            }

            @Override
            public void onProductsFetchedFailed(Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    public void setAllProducts(List<Products> products) {
        allProducts = products;
        filteredProducts.setValue(products);
    }

    public void filteredItems(String query) {
        if (query == null || query.isEmpty()) {
            filteredProducts.setValue(allProducts);
        } else {
            List<Products> filtered = new ArrayList<>();
            for (Products product : allProducts) {
                if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                    filtered.add(product);
                }
            }
            filteredProducts.setValue(filtered);
        }
    }
}