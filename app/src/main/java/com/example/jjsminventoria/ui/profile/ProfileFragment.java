package com.example.jjsminventoria.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.fragment.app.FragmentTransaction;

import com.example.jjsminventoria.R;
import com.example.jjsminventoria.databinding.FragmentProfileBinding;
import com.example.jjsminventoria.fragment_settings;
import com.example.jjsminventoria.ui.UserList.UserListFragment;
import com.example.jjsminventoria.ui.settings.SettingsFragment;
/*
import com.example.jjsminventoria.
*/

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel notificationsViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.profileTitle;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // ✅ Add Click Listeners
        binding.settings.setOnClickListener(v -> {
            replaceFragment(new SettingsFragment());
        });

        //binding.
        binding.ivUsersList.setOnClickListener(v->{
            replaceFragment(new UserListFragment());
        });

       /* binding.users.setOnClickListener(v -> {
            replaceFragment(new UsersFragment());
        });*/

        return root;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_activity_main_menu_bottom_tabs, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
