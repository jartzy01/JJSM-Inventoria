package com.example.jjsminventoria.adpters;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jjsminventoria.R;
import com.example.jjsminventoria.database.FirebaseConnection;
import com.example.jjsminventoria.fragment_item_editing;

import java.util.List;

import model.Products;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Products> productList;
    private final String categoryName;
    private final Runnable refreshCallback;
    private int selectedPosition = -1;

    public ProductAdapter(List<Products> productList, String categoryName, Runnable refreshCallback) {
        this.productList = productList;
        this.categoryName = categoryName;
        this.refreshCallback = refreshCallback;
    }

    public void setProductList(List<Products> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Products product = productList.get(position);
        holder.itemName.setText(product.getName());

        holder.actionContainer.setVisibility(selectedPosition == position ? View.VISIBLE : View.INVISIBLE);

        holder.itemContainer.setOnClickListener(v -> {
            selectedPosition = (selectedPosition == position) ? -1 : position;
            notifyDataSetChanged();
        });

        holder.editIcon.setOnClickListener(v -> {
            Fragment editFragment = fragment_item_editing.newInstance(product, categoryName);
            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main_menu_bottom_tabs, editFragment)
                    .addToBackStack(null)
                    .commit();
        });

        holder.deleteIcon.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete \"" + product.getName() + "\"?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseConnection.getInstance()
                                .getRootDb()
                                .child("Categories")
                                .child(categoryName)
                                .child("Products")
                                .child(product.getId())
                                .removeValue()
                                .addOnSuccessListener(unused -> {
                                    // ✅ Log the delete action here
                                    FirebaseConnection.getInstance().logHistory(
                                            "Delete Item",
                                            "Deleted item \"" + product.getName() + "\" from category \"" + categoryName + "\""
                                    );

                                    Toast.makeText(v.getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
                                    if (refreshCallback != null) refreshCallback.run();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(v.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        ImageView deleteIcon, editIcon;
        View actionContainer, itemContainer;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
            editIcon = itemView.findViewById(R.id.editIcon);
            actionContainer = itemView.findViewById(R.id.actionContainer);
            itemContainer = itemView; // root layout
        }
    }
}
