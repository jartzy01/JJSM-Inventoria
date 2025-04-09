package com.example.jjsminventoria.adpters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jjsminventoria.R;
import com.example.jjsminventoria.database.FirebaseConnection;

import java.util.List;

import model.Category;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;
    private int selectedPosition = -1;

    public CategoryAdapter(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public void setCategoryList(List<Category> updatedList) {
        this.categoryList = updatedList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.bind(category);

        // Toggle visibility based on selected position
        holder.actionContainer.setVisibility(position == selectedPosition ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            if (selectedPosition == position) {
                selectedPosition = -1; // Unselect if same
            } else {
                selectedPosition = position;
            }
            notifyDataSetChanged();
        });

        holder.deleteIcon.setOnClickListener(v -> {
            showDeleteConfirmation(holder.itemView.getContext(), category);
        });
    }

    private void showDeleteConfirmation(Context context, Category category) {
        SpannableString redText = new SpannableString(category.getName());
        redText.setSpan(new ForegroundColorSpan(Color.RED), 0, redText.length(), 0);

        new AlertDialog.Builder(context)
                .setTitle("Delete Category")
                .setMessage(TextUtils.concat("Are you sure you want to delete ", redText, "?"))
                .setPositiveButton("Delete", (dialog, which) -> {
                    FirebaseConnection.getInstance()
                            .getCategoryDb()
                            .child(category.getName())
                            .removeValue();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        LinearLayout actionContainer;
        ImageView editIcon, deleteIcon;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            actionContainer = itemView.findViewById(R.id.actionContainer);
            editIcon = itemView.findViewById(R.id.editContainer);
            deleteIcon = itemView.findViewById(R.id.deleteContainer);
        }

        void bind(Category category) {
            categoryName.setText(category.getName());
        }
    }
}
