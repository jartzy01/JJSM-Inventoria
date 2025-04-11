package com.example.jjsminventoria.ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.jjsminventoria.databinding.FragmentDashboardBinding;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dashboardViewModel.getInventoryQty().observe(getViewLifecycleOwner(), inventoryQty -> {
            binding.tvInventoryQty.setText(String.valueOf(inventoryQty));
        });

        dashboardViewModel.getTotalProducts().observe(getViewLifecycleOwner(), totalProducts -> {
            binding.tvTotalProducts.setText(String.valueOf(totalProducts));
            updatePieChart();
        });

        dashboardViewModel.getTotalCategories().observe(getViewLifecycleOwner(), totalCategories -> {
            binding.tvTotCategories.setText(String.valueOf(totalCategories));
        });

        dashboardViewModel.getLowStockCount().observe(getViewLifecycleOwner(), lowStock -> {
            binding.tvLowStockValue.setText(String.valueOf(lowStock));
            updatePieChart();
        });

        dashboardViewModel.getOutOfStockCount().observe(getViewLifecycleOwner(), outStock -> {
            binding.tvOutOfStockValue.setText(String.valueOf(outStock));
            updatePieChart();
        });

        initializePieChart();

        return root;
    }

    private void initializePieChart() {
        PieChart pieChart = binding.chart1;

        List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(0f, "In Stock"));
        pieEntries.add(new PieEntry(0f, "Out of Stock"));
        pieEntries.add(new PieEntry(0f, "Low Stock"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Stock Status");

        List<Integer> color = new ArrayList<>();
        color.add(Color.RED);
        color.add(Color.CYAN);
        color.add(Color.YELLOW);

        pieDataSet.setColors(color);

        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(20f);

        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(20f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private void updatePieChart() {
        PieChart pieChart = binding.chart1;

        Integer totalProducts = dashboardViewModel.getTotalProducts().getValue();
        Integer outStock = dashboardViewModel.getOutOfStockCount().getValue();
        Integer lowStock = dashboardViewModel.getLowStockCount().getValue();

        int inStock = 0;
        if (totalProducts != null && outStock != null && lowStock != null) {
            inStock = totalProducts - outStock - lowStock;
        }

        List<PieEntry> pieEntries = new ArrayList<>();

        if (inStock > 0) {
            pieEntries.add(new PieEntry(inStock, "In Stock"));
        }
        if (outStock != null && outStock > 0) {
            pieEntries.add(new PieEntry(outStock, "Out Stock"));
        }
        if (lowStock != null && lowStock > 0) {
            pieEntries.add(new PieEntry(lowStock, "Low Stock"));
        }

        if (pieEntries.isEmpty()) {
            return;
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Stock Status");

        List<Integer> color = new ArrayList<>();
        color.add(Color.RED);
        color.add(Color.CYAN);
        color.add(Color.YELLOW);

        pieDataSet.setColors(color);

        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setTextSize(16f);
        legend.setFormSize(16f);

        pieChart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}