package com.telescope.maasid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.telescope.maasid.databinding.FragmentDatabaseBinding;

public class DatabaseFragment extends Fragment {

    private FragmentDatabaseBinding binding;

    public DatabaseFragment() {
        // Required empty public constructor
    }

    public static DatabaseFragment newInstance(String param1, String param2) {
        DatabaseFragment fragment = new DatabaseFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment with view binding
        binding = FragmentDatabaseBinding.inflate(inflater, container, false);

        // Initialize data arrays
        String[] MaasName = {"Bahu", "Borali", "Eilish", "Sitol", "Rou", "Magur", "Bhagon", "Mirika", "Kuhi", "Rupchanda"};
        int[] flowerImages = {R.drawable.bahu, R.drawable.borali, R.drawable.eilish, R.drawable.sitol, R.drawable.rou, R.drawable.magur, R.drawable.bhagon, R.drawable.mirika, R.drawable.kuhi, R.drawable.rupchanda};

        // Set up adapter for GridView
        GridAdapter gridAdapter = new GridAdapter(requireContext(), MaasName, flowerImages);
        binding.gridView.setAdapter(gridAdapter);

        // Set item click listener
        binding.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(requireContext(), "You Clicked on " + MaasName[position], Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // Avoid memory leaks
    }
}
