package com.example.jjsminventoria;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jjsminventoria.adpters.HistoryAdapter;
import com.example.jjsminventoria.database.FirebaseConnection;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import model.HistoryItem;

public class fragment_history extends Fragment {

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private List<HistoryItem> historyList = new ArrayList<>();

    public fragment_history() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.categoryRecyclerView);
        EditText searchBar = view.findViewById(R.id.searchBar);

        adapter = new HistoryAdapter(historyList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Load current user's history
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseConnection.getInstance().getUserDb()
                .child(uid)
                .child("History")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        historyList.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            HistoryItem item = snap.getValue(HistoryItem.class);
                            if (item != null) {
                                historyList.add(item);
                            }
                        }
                        adapter.setHistoryList(historyList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load history", Toast.LENGTH_SHORT).show();
                    }
                });

        // Add search functionality
        searchBar.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterHistory(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        TextView companyNameTextView = view.findViewById(R.id.companyNameTextView);

        FirebaseConnection.getInstance().getCompanyNameOnce(name -> {
            companyNameTextView.setText(name);
        });

        TextView tabCategories = view.findViewById(R.id.tabCategories);
        tabCategories.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main_menu_bottom_tabs, new fragment_categories())
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void filterHistory(String query) {
        List<HistoryItem> filteredList = new ArrayList<>();
        for (HistoryItem item : historyList) {
            if (item.getMessage().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.setHistoryList(filteredList);
    }

}
