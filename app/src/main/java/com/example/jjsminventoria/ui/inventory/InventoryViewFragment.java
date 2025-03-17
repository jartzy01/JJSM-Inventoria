package com.example.jjsminventoria.ui.inventory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

//import com.example.jjsminventoria.databinding.FragmentInventoryBinding;

public class InventoryViewFragment extends Fragment {

    //private FragmentInventoryBinding binding;

//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        InventoryViewModel dashboardViewModel =
//                new ViewModelProvider(this).get(InventoryViewModel.class);
//
//        //binding = FragmentInventoryBinding.inflate(inflater, container, false);
//        //View root = binding.getRoot();
//
//        //final TextView textView = binding.textDashboard;
//        //dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
//        return root;
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //binding = null;
    }
}