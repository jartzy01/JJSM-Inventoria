package com.example.jjsminventoria.ui.settings;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jjsminventoria.R;

public class SettingsFragment extends Fragment {

    private SettingsViewModel mViewModel;

    private EditText etSName, etSEmail, etSPassword;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        etSName = view.findViewById(R.id.etSName);
        etSEmail = view.findViewById(R.id.etCAEmail);
        etSPassword = view.findViewById(R.id.etSPassword);

        mViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        mViewModel.getUserInfo().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                etSName.setHint(user.getName());
                etSEmail.setHint(user.getEmail());
                etSPassword.setHint("********");
            }
        });
    }

}