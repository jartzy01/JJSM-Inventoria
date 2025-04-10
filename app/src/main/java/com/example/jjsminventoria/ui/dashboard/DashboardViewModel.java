package com.example.jjsminventoria.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jjsminventoria.database.FirebaseConnection;

import java.util.List;

import model.Products;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<Integer> inventoryQtc = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalProducts = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalCategories = new MutableLiveData<>();
    private final MutableLiveData<Integer> outOfStockCount = new MutableLiveData<>();
    private final MutableLiveData<Integer> lowStockCount = new MutableLiveData<>();

    public DashboardViewModel() {
        loadProductsFromFirebase();
    }

    public LiveData<Integer> getInventoryQty() {
        return inventoryQtc;
    }

    public LiveData<Integer> getTotalProducts() {
        return totalProducts;
    }

    public LiveData<Integer> getTotalCategories() {
        return totalCategories;
    }

    public LiveData<Integer> getLowStockCount() {
        return lowStockCount;
    }

    public LiveData<Integer> getOutOfStockCount() {
        return outOfStockCount;
    }

    private void loadProductsFromFirebase() {
        FirebaseConnection.getInstance().fetchProducts(new FirebaseConnection.FetchProductsCallback() {
            @Override
            public void onProductsFetched(List<Products> products) {
                setAllProducts(products);
            }

            @Override
            public void onProductsFetchedFailed(Exception exception) {
                exception.printStackTrace();
            }
        });

        FirebaseConnection.getInstance().fetchCategories(new FirebaseConnection.FetchCategoriesCallBack() {
            @Override
            public void onCategoriesFetched(List<String> categories) {
                setTotalCategories(categories);
            }

            @Override
            public void onCategoriesFetchedFailed(Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    private void setAllProducts(List<Products> products) {
        int totalP = products.size();
        int lowStock = 0;
        int outStock = 0;
        int totalQty = 0;

        for (Products product : products) {
            totalQty += product.getQty();
            if (product.getQty() == 0) {
                outStock++;
            } else if (product.getQty() < 10) {
                lowStock++;
            }
        }

        inventoryQtc.setValue(totalQty);
        totalProducts.setValue(totalP);
        lowStockCount.setValue(lowStock);
        outOfStockCount.setValue(outStock);
    }

    private void setTotalCategories(List<String> categories) {
        totalCategories.setValue(categories.size());
    }
}