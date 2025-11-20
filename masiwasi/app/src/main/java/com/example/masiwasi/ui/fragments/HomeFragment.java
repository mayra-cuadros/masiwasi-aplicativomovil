package com.example.masiwasi.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.masiwasi.R;
import com.example.masiwasi.ui.activities.DetailActivity;
import com.google.android.material.chip.Chip;


public class HomeFragment extends Fragment {

    private viewmodels.HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private adapters.MascotaAdapter adapter;

    private Chip chipAll, chipPerro, chipGato;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewDestinations);

        chipAll = view.findViewById(R.id.chipAll);
        chipPerro = view.findViewById(R.id.chipPerro);
        chipGato = view.findViewById(R.id.chipGato);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);

        homeViewModel = new ViewModelProvider(this).get(viewmodels.HomeViewModel.class);

        homeViewModel.getMascotas().observe(getViewLifecycleOwner(), mascotas -> {
            adapter = new adapters.MascotaAdapter(requireContext(), mascotas, false, mascota -> {
                Toast.makeText(requireContext(), "Ver: " + mascota.getNombre(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(requireContext(), DetailActivity.class);
                intent.putExtra(DetailActivity.EXTRA_MASCOTA, mascota);
                startActivity(intent);
            });
            recyclerView.setAdapter(adapter);
        });

        chipAll.setOnClickListener(v -> homeViewModel.filterBy("Todos"));
        chipPerro.setOnClickListener(v -> homeViewModel.filterBy("Perro"));
        chipGato.setOnClickListener(v -> homeViewModel.filterBy("Gato"));

        return view;
    }
}
