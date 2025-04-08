package com.example.jjsminventoria;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jjsminventoria.adpters.CategoryAdapter;
import com.example.jjsminventoria.database.FirebaseConnection;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import model.Category;

public class fragment_categories extends Fragment {

    private Button addCategoryButton;
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter adapter;
    private List<Category> categoryList = new ArrayList<>();


    public fragment_categories() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inventory_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toast.makeText(getContext(), "Fragment loaded!", Toast.LENGTH_SHORT).show(); // ← Test this first



        addCategoryButton = view.findViewById(R.id.addCategoryButton);
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);

        adapter = new CategoryAdapter(categoryList);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        categoryRecyclerView.setAdapter(adapter);

        // Show dialog when + is clicked
        addCategoryButton.setOnClickListener(v -> showAddCategoryDialog());

        // Fetch and show categories from Firebase
        FirebaseConnection.getInstance().getCategoryDb()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Toast.makeText(getContext(), "Data fetched!", Toast.LENGTH_SHORT).show(); // <--- Add this

                        categoryList.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Category category = snap.getValue(Category.class);
                            if (category != null) {
                                categoryList.add(category);
                            }
                        }
                        adapter.setCategoryList(categoryList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("New Category");

        final EditText input = new EditText(getContext());
        input.setHint("Enter category name");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Category name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            String id = FirebaseConnection.getInstance().getCategoryDb().push().getKey();
            Category newCategory = new Category(id, name);

            FirebaseConnection.getInstance()
                    .getCategoryDb()
                    .child(id)
                    .setValue(newCategory)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(getContext(), "Category added!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
