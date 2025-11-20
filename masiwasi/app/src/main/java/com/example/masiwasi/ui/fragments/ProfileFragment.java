package com.example.masiwasi.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.masiwasi.R;
import com.example.masiwasi.ui.activities.NewPublicationActivity;

import java.util.ArrayList;
import java.util.List;

import adapters.MascotaAdapter;
import models.Mascota;

public class ProfileFragment extends Fragment {

    private ImageView imgUserProfile;
    private TextView txtUserName, txtUserEmail, txtUserLocation;
    private Button btnEditProfile, btnNewPublication, btnLogout;
    private RecyclerView rvUserPets;

    private List<Mascota> mascotasList = new ArrayList<>();
    private MascotaAdapter adapter;
    private boolean modoEdicion = false;

    public ProfileFragment() {}

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imgUserProfile = view.findViewById(R.id.imgUserProfile);
        txtUserName = view.findViewById(R.id.txtUserName);
        txtUserEmail = view.findViewById(R.id.txtUserEmail);
        txtUserLocation = view.findViewById(R.id.txtUserLocation);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnNewPublication = view.findViewById(R.id.btnNewPublication);
        btnLogout = view.findViewById(R.id.btnLogout);
        rvUserPets = view.findViewById(R.id.rvUserPets);

        rvUserPets.setLayoutManager(new LinearLayoutManager(getContext()));

        // Datos de prueba
        mascotasList.add(new Mascota("1","Demi", "8 meses", "Hembra", "Juguetona y dulce", "Perro", "Marrón", "https://misimagenes.com/demi.jpg"));
        mascotasList.add(new Mascota("2","Fevi", "2 meses", "Macho", "Muy curioso y activo", "Gato", "Gris", "https://misimagenes.com/fevi.jpg"));

        adapter = new MascotaAdapter(getContext(), mascotasList, modoEdicion, mascota -> {
            if (modoEdicion) {
                Intent intent = new Intent(getContext(), NewPublicationActivity.class);
                intent.putExtra("mascota_id", mascota.getId());
                intent.putExtra("mascota_nombre", mascota.getNombre());
                intent.putExtra("mascota_edad", mascota.getEdad());
                intent.putExtra("mascota_sexo", mascota.getSexo());
                intent.putExtra("mascota_descripcion", mascota.getDescripcion());
                intent.putExtra("mascota_categoria", mascota.getCategoria());
                intent.putExtra("mascota_color", mascota.getColor());
                intent.putExtra("mascota_imageUrl", mascota.getImageUrl());
                startActivity(intent);
            }
        });

        rvUserPets.setAdapter(adapter);

        txtUserName.setText("Mark Z.");
        txtUserEmail.setText("mark@gmail.com");
        txtUserLocation.setText("Lima, Perú");
        imgUserProfile.setImageResource(R.drawable.ic_launcher_foreground);

        btnEditProfile.setOnClickListener(v -> {
            modoEdicion = !modoEdicion;
            adapter.setModoEdicion(modoEdicion);
        });

        btnNewPublication.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NewPublicationActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
