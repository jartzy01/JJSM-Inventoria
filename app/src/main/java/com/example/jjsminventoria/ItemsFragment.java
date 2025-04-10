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

import com.example.jjsminventoria.adpters.ItemAdapter;
import com.example.jjsminventoria.database.FirebaseConnection;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import model.Item;

public class ItemsFragment extends Fragment {

    private static final String ARG_CATEGORY_ID = "category_id";
    private String categoryId;
    private RecyclerView itemRecyclerView;
    private EditText searchBar;
    private ItemAdapter itemAdapter;
    private List<Item> itemList = new ArrayList<>();
    private Button addItemButton;


    public ItemsFragment() {}

    public static ItemsFragment newInstance(String categoryId) {
        ItemsFragment fragment = new ItemsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_ID, categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getString(ARG_CATEGORY_ID);
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
        searchBar = view.findViewById(R.id.itemsSearchBar); // ✅ Corrected ID

        itemAdapter = new ItemAdapter(itemList);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        itemRecyclerView.setAdapter(itemAdapter);

        // Load items from Firebase
        loadItems();

        // Setup search bar
        searchBar.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterItems(s.toString());
            }
        });

        // Header Tabs
        TextView backTab = view.findViewById(R.id.itemsBack);
        backTab.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main_menu_bottom_tabs, new fragment_categories())
                    .addToBackStack(null)
                    .commit();
        });

        TextView historyTab = view.findViewById(R.id.itemsTab);  // if you meant the "Items" tab
        historyTab.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main_menu_bottom_tabs, new fragment_history())
                    .addToBackStack(null)
                    .commit();
        });

        addItemButton = view.findViewById(R.id.addItemButton);

        addItemButton.setOnClickListener(v -> {
            showAddItemDialog();
        });
    }

    private void loadItems() {
        FirebaseConnection.getInstance().getItemDb()
                .orderByChild("categoryId")
                .equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        itemList.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Item item = snap.getValue(Item.class);
                            if (item != null) {
                                itemList.add(item);
                            }
                        }
                        itemAdapter.setItemList(itemList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Optional: Toast or log error
                    }
                });
    }

    private void filterItems(String query) {
        List<Item> filteredList = new ArrayList<>();
        for (Item item : itemList) {
            if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        itemAdapter.setItemList(filteredList);
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add New Item");

        final EditText input = new EditText(getContext());
        input.setHint("Enter item name");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String itemName = input.getText().toString().trim();
            if (itemName.isEmpty()) {
                Toast.makeText(getContext(), "Item name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            String itemId = FirebaseConnection.getInstance().getItemDb().push().getKey();
            if (itemId != null) {
                model.Item newItem = new model.Item(itemId, itemName, categoryId);
                FirebaseConnection.getInstance().getItemDb()
                        .child(itemId)
                        .setValue(newItem)
                        .addOnSuccessListener(unused -> Toast.makeText(getContext(), "Item added", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
