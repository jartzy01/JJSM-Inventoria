package com.example.jjsminventoria.ui.inventory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.jjsminventoria.adpters.ProductAdapter;
import com.example.jjsminventoria.databinding.FragmentInventoryHomeBinding;

import java.util.ArrayList;
import java.util.List;

import model.Products;

public class InventoryHomeViewFragment extends Fragment {

    private FragmentInventoryHomeBinding binding;
    private InventoryHomeViewModel inventoryHomeViewModel;
    private ProductAdapter productAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        InventoryHomeViewModel viewModel =
                new ViewModelProvider(this).get(InventoryHomeViewModel.class);

        binding = FragmentInventoryHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        inventoryHomeViewModel = viewModel;
        initialize();
        return root;
    }

    private void initialize() {
        List<Products> productsList = loadMockInventory();
        inventoryHomeViewModel.setAllProducts(productsList);

        productAdapter = new ProductAdapter(new ArrayList<>(), "MockCategory", () -> {});
        binding.categoryRecyclerView.setAdapter(productAdapter);

        // Observe the filtered products and update the adapter
        inventoryHomeViewModel.getFilteredProducts().observe(getViewLifecycleOwner(), filteredList -> {
            productAdapter.setProductList(filteredList);
        });

        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                inventoryHomeViewModel.filteredItems(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                inventoryHomeViewModel.filteredItems(newText);
                return false;
            }
        });
    }

    private List<Products> loadMockInventory() {
        List<Products> products = new ArrayList<>();
        products.add(new Products("Laptop", "16GB Ram", 5, 1.2));
        return products;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
