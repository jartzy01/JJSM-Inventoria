package com.example.jjsminventoria;

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

import com.example.jjsminventoria.Builder.ProductBuilder;
import com.example.jjsminventoria.database.FirebaseConnection;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import model.Products;

public class fragment_item_editing extends Fragment {

    private static final String ARG_PRODUCT = "product";
    private static final String ARG_CATEGORY = "category";

    private Products product;
    private String categoryName;

    public fragment_item_editing() {}

    public static fragment_item_editing newInstance(Products product, String categoryName) {
        fragment_item_editing fragment = new fragment_item_editing();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCT, product);
        args.putString(ARG_CATEGORY, categoryName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = (Products) getArguments().getSerializable(ARG_PRODUCT);
            categoryName = getArguments().getString(ARG_CATEGORY, "Default");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_editing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText itemTitle = view.findViewById(R.id.itemTitle);
        EditText description = view.findViewById(R.id.descriptionInput);
        EditText id = view.findViewById(R.id.idInput);
        EditText stock = view.findViewById(R.id.stockInput);
        EditText price = view.findViewById(R.id.PriceInput);
        EditText discount = view.findViewById(R.id.discountInput);

        TextView mainSection = view.findViewById(R.id.mainSection);
        TextView subSection1 = view.findViewById(R.id.subSection1);
        TextView subSection2 = view.findViewById(R.id.subSection2);

        mainSection.setText("Categories /");
        subSection1.setText(categoryName + " /");

        if (product != null) {
            subSection2.setText(product.getName() != null ? product.getName() : "Unnamed");
            subSection2.setVisibility(View.VISIBLE);

            itemTitle.setText(product.getName());
            description.setText(product.getDesc());
            id.setText(product.getId() != null ? product.getId() : "");
            id.setEnabled(false);
            stock.setText(String.valueOf(product.getQty()));
            price.setText(String.valueOf(product.getPrice()));
            discount.setText(String.valueOf(product.getDiscount()));
        } else {
            subSection2.setVisibility(View.GONE);
            id.setEnabled(false);
        }

        TextView historyTab = view.findViewById(R.id.tabHistory);
        historyTab.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main_menu_bottom_tabs, new fragment_history())
                    .addToBackStack(null)
                    .commit();
        });

        TextView companyNameTextView = view.findViewById(R.id.companyNameTextView);

        FirebaseConnection.getInstance().getCompanyNameOnce(name -> {
            companyNameTextView.setText(name);
        });

        Button saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            String name = itemTitle.getText().toString().trim();
            String descriptionText = description.getText().toString().trim();
            String stockText = stock.getText().toString().trim();
            String priceText = price.getText().toString().trim();
            String discountText = discount.getText().toString().trim();

            try {
                int qty = Integer.parseInt(stockText);
                double priceVal = Double.parseDouble(priceText);
                double discountVal = Double.parseDouble(discountText);
                String placeholderImg = "no_image";

                Products builtProduct = new ProductBuilder()
                        .setName(name.isEmpty() ? "Untitled" : name)
                        .setDesc(descriptionText)
                        .setQty(qty)
                        .setWeight(priceVal)
                        .setImg(placeholderImg)
                        .build();

                builtProduct.setPrice(priceVal);
                builtProduct.setDiscount(discountVal);

                String productKey = (product != null && product.getId() != null)
                        ? product.getId()
                        : FirebaseConnection.getInstance().getRootDb()
                        .child("Categories")
                        .child(categoryName)
                        .child("Products")
                        .push()
                        .getKey();

                if (productKey != null) {
                    builtProduct.setId(productKey);
                    FirebaseConnection.getInstance().getRootDb()
                            .child("Categories")
                            .child(categoryName)
                            .child("Products")
                            .child(productKey)
                            .setValue(builtProduct)
                            .addOnSuccessListener(task -> {
                                boolean isEdit = (product != null && product.getId() != null);
                                String action = isEdit ? "Modify Item" : "Create Item";
                                String message = (isEdit ? "Modified" : "Created") + " item \"" + builtProduct.getName() + "\" in category \"" + categoryName + "\"";
                                FirebaseConnection.getInstance().logHistory(action, message);

                                Toast.makeText(getContext(), "Product saved!", Toast.LENGTH_SHORT).show();
                                navigateBackToItems();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }

            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please fill all numeric fields correctly.", Toast.LENGTH_SHORT).show();
            }
        });

        Button deleteButton = view.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> {
            if (product != null && product.getId() != null) {
                new android.app.AlertDialog.Builder(getContext())
                        .setTitle("Delete Item")
                        .setMessage("Are you sure you want to delete this item?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            FirebaseConnection.getInstance().getRootDb()
                                    .child("Categories")
                                    .child(categoryName)
                                    .child("Products")
                                    .child(product.getId())
                                    .removeValue()
                                    .addOnSuccessListener(unused -> {
                                        FirebaseConnection.getInstance().logHistory(
                                                "Delete Item",
                                                "Deleted item \"" + product.getName() + "\" from category \"" + categoryName + "\""
                                        );
                                        Toast.makeText(getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
                                        navigateBackToItems();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                navigateBackToItems();
            }
        });
    }

    private void navigateBackToItems() {
        Fragment itemsFragment = ItemsFragment.newInstance(categoryName);
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main_menu_bottom_tabs, itemsFragment)
                .addToBackStack(null)
                .commit();
    }
}
