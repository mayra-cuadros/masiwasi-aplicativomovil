package com.example.masiwasimovil.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.masiwasimovil.R;
import com.example.masiwasimovil.activities.DetailActivity;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import adapters.MascotaAdapter;
import models.Mascota;

public class HomeFragment extends Fragment {

    private viewmodels.HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private MascotaAdapter adapter;
    private Chip chipAll, chipPerro, chipGato;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inicializar vistas
        recyclerView = view.findViewById(R.id.recyclerViewPublications);
        chipAll = view.findViewById(R.id.chipAll);
        chipPerro = view.findViewById(R.id.chipPerro);
        chipGato = view.findViewById(R.id.chipGato);

        // Configuración del RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new MascotaAdapter(requireContext(), new ArrayList<>(), false, mascota -> {
            Intent intent = new Intent(requireContext(), DetailActivity.class);
            intent.putExtra("nombre", mascota.getNombre());
            intent.putExtra("descripcion", mascota.getDescripcion());
            intent.putExtra("imagenUrl", mascota.getImageUrl());
            intent.putExtra("sexo", mascota.getSexo());
            intent.putExtra("edad", mascota.getEdad());
            intent.putExtra("categoria", mascota.getCategoria());
            intent.putExtra("color", mascota.getColor());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        homeViewModel = new ViewModelProvider(this).get(viewmodels.HomeViewModel.class);

        // Observamos los datos
        homeViewModel.getMascotas().observe(getViewLifecycleOwner(), mascotas -> {
            if (mascotas != null) {
                adapter.updateList(mascotas);
            }
        });

        chipAll.setOnClickListener(v -> homeViewModel.filterBy("Todos"));
        chipPerro.setOnClickListener(v -> homeViewModel.filterBy("Perro"));
        chipGato.setOnClickListener(v -> homeViewModel.filterBy("Gato"));

        return view;
    }
}