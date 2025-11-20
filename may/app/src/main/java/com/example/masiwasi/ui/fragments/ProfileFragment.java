package com.example.masiwasi.ui.fragments;

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

import com.example.masiwasi.R;

import java.util.ArrayList;
import java.util.List;

import models.Mascota;

public class ProfileFragment extends Fragment {

    private ImageView imgUserProfile;
    private TextView txtUserName, txtUserEmail, txtUserLocation;
    private Button btnEditProfile, btnNewPublication, btnLogout;
    private RecyclerView rvUserPets;

    // Lista de mascotas del usuario
    private List<Mascota> mascotasList = new ArrayList<>();

    public ProfileFragment() {
        // Constructor vacío obligatorio
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar layout del perfil
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Inicializar vistas
        imgUserProfile = view.findViewById(R.id.imgUserProfile);
        txtUserName = view.findViewById(R.id.txtUserName);
        txtUserEmail = view.findViewById(R.id.txtUserEmail);
        txtUserLocation = view.findViewById(R.id.txtUserLocation);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnNewPublication = view.findViewById(R.id.btnNewPublication);
        btnLogout = view.findViewById(R.id.btnLogout);
        rvUserPets = view.findViewById(R.id.rvUserPets);

        // Configurar RecyclerView
        rvUserPets.setLayoutManager(new LinearLayoutManager(getContext()));
        // Aquí iría el adapter, por ejemplo:
        // rvUserPets.setAdapter(new MascotaAdapter(mascotasList));

        // TODO: Cargar datos de usuario y mascotas
        // txtUserName.setText(usuario.getNombre());
        // txtUserEmail.setText(usuario.getCorreo());
        // txtUserLocation.setText(usuario.getCiudad() + ", " + usuario.getPais());
        // imgUserProfile.setImageBitmap(usuario.getImagen());

        return view;
    }
}
