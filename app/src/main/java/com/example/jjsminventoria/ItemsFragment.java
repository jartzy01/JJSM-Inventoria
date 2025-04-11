package com.example.jjsminventoria;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jjsminventoria.adpters.ProductAdapter;
import com.example.jjsminventoria.database.FirebaseConnection;

import java.util.ArrayList;
import java.util.List;

import model.Products;

public class ItemsFragment extends Fragment {

    private static final String ARG_CATEGORY_NAME = "category_name";
    private String categoryName;

    private RecyclerView itemRecyclerView;
    private EditText searchBar;
    private Button addItemButton;
    private ProductAdapter itemAdapter;
    private List<Products> itemList = new ArrayList<>();

    public ItemsFragment() {}

    public static ItemsFragment newInstance(String categoryName) {
        ItemsFragment fragment = new ItemsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_NAME, categoryName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryName = getArguments().getString(ARG_CATEGORY_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        itemRecyclerView = view.findViewById(R.id.itemsRecyclerView);
        searchBar = view.findViewById(R.id.itemsSearchBar);
        addItemButton = view.findViewById(R.id.addItemButton);

        // ✅ Set the breadcrumb category name dynamically
        TextView subSection1 = view.findViewById(R.id.subSection1);
        subSection1.setText(categoryName + " /");

        itemAdapter = new ProductAdapter(itemList, categoryName, this::loadProducts);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        itemRecyclerView.setAdapter(itemAdapter);

        loadProducts();

        searchBar.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }
        });

        TextView backTab = view.findViewById(R.id.itemsBack);
        backTab.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main_menu_bottom_tabs, new fragment_categories())
                    .addToBackStack(null)
                    .commit();
        });

        TextView historyTab = view.findViewById(R.id.itemsTab);
        historyTab.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main_menu_bottom_tabs, new fragment_history())
                    .addToBackStack(null)
                    .commit();
        });

        addItemButton.setOnClickListener(v -> {
            Fragment createFragment = fragment_item_editing.newInstance(null, categoryName);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main_menu_bottom_tabs, createFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void loadProducts() {
        FirebaseConnection.getInstance().fetchProductsUnderCategory(categoryName, new FirebaseConnection.FetchProductsCallback() {
            @Override
            public void onProductsFetched(List<Products> products) {
                itemList.clear();
                itemList.addAll(products);
                itemAdapter.setProductList(itemList);
            }

            @Override
            public void onProductsFetchedFailed(Exception exception) {
                Toast.makeText(getContext(), "Error loading products", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterProducts(String query) {
        List<Products> filtered = new ArrayList<>();
        for (Products product : itemList) {
            if (product.getName() != null && product.getName().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(product);
            }
        }
        itemAdapter.setProductList(filtered);
    }

    private void showAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add New Product");

        final EditText input = new EditText(getContext());
        input.setHint("Enter product name");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Product name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            Products newProduct = new Products();
            newProduct.setName(name);
            newProduct.setQty(1);
            newProduct.setWeight(0.0);
            newProduct.setDesc("New product");

            String key = FirebaseConnection.getInstance().getRootDb()
                    .child("Categories")
                    .child(categoryName)
                    .child("Products")
                    .push().getKey();

            if (key != null) {
                newProduct.setId(key);

                FirebaseConnection.getInstance()
                        .getRootDb()
                        .child("Categories")
                        .child(categoryName)
                        .child("Products")
                        .child(key)
                        .setValue(newProduct)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(getContext(), "Product added", Toast.LENGTH_SHORT).show();
                            loadProducts();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
