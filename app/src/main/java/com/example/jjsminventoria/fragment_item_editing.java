package com.example.jjsminventoria;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import model.Products;

public class fragment_item_editing extends Fragment {

    private static final String ARG_PRODUCT = "product";
    private Products product;

    public fragment_item_editing() {}

    // ✅ Factory method that allows null (for creation)
    public static fragment_item_editing newInstance(Products product) {
        fragment_item_editing fragment = new fragment_item_editing();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCT, product); // Can be null
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = (Products) getArguments().getSerializable(ARG_PRODUCT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_editing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView itemTitle = view.findViewById(R.id.itemTitle);
        EditText description = view.findViewById(R.id.descriptionInput);
        EditText id = view.findViewById(R.id.idInput);
        EditText stock = view.findViewById(R.id.stockInput);
        EditText price = view.findViewById(R.id.PriceInput);
        EditText discount = view.findViewById(R.id.discountInput);

        // ✅ If we're editing a product, fill in the fields
        if (product != null) {
            itemTitle.setText(product.getName());
            description.setText(product.getDesc());
            id.setText(product.getId() != null ? product.getId() : ""); // String now
            stock.setText(String.valueOf(product.getQty()));
            price.setText("N/A");     // Optional: update if you add price field to Products
            discount.setText("N/A");  // Optional: update if you add discount field
        } else {
            // ✅ New item creation mode – leave fields empty or set defaults
            id.setEnabled(false); // maybe hide or auto-generate on save
        }
    }
}
