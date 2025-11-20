package com.example.masiwasi.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.example.masiwasi.ui.activities.DetailActivity;
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

    private viewmodels.ProfileViewModel profileViewModel;
    private RecyclerView recyclerView;

    private ActivityResultLauncher<Intent> lNewPublication;

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
                lNewPublication.launch(intent);
            }
        });

        adapter = new MascotaAdapter(getContext(), mascotasList, modoEdicion, mascota -> {
            if (modoEdicion) {
                // Editar publicación
                Intent intent = new Intent(getContext(), NewPublicationActivity.class);
                intent.putExtra("mascota_id", mascota.getId());
                intent.putExtra("mascota_nombre", mascota.getNombre());
                intent.putExtra("mascota_edad", mascota.getEdad());
                intent.putExtra("mascota_sexo", mascota.getSexo());
                intent.putExtra("mascota_descripcion", mascota.getDescripcion());
                intent.putExtra("mascota_categoria", mascota.getCategoria());
                intent.putExtra("mascota_color", mascota.getColor());
                intent.putExtra("mascota_imageUrl", mascota.getImageUrl());
                lNewPublication.launch(intent);
            } else {
                // Ver detalles
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra(DetailActivity.EXTRA_MASCOTA, mascota);
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

        lNewPublication = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String id = data.getStringExtra("mascota_id");
                        String nombre = data.getStringExtra("mascota_nombre");
                        String edad = data.getStringExtra("mascota_edad");
                        String sexo = data.getStringExtra("mascota_sexo");
                        String categoria = data.getStringExtra("mascota_categoria");
                        String color = data.getStringExtra("mascota_color");
                        String descripcion = data.getStringExtra("mascota_descripcion");
                        String imageUrl = data.getStringExtra("mascota_imageUrl");

                        Mascota nuevaMascota = new Mascota(id, nombre, edad, sexo, descripcion, categoria, color, imageUrl);

                        boolean encontrada = false;
                        for (int i = 0; i < mascotasList.size(); i++) {
                            if (mascotasList.get(i).getId().equals(id)) {
                                mascotasList.set(i, nuevaMascota); // actualización
                                encontrada = true;
                                break;
                            }
                        }

                        if (!encontrada) {
                            mascotasList.add(nuevaMascota); // nueva publicación
                        }

                        adapter.notifyDataSetChanged();
                    }
                }
        );

        btnNewPublication.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NewPublicationActivity.class);
            lNewPublication.launch(intent);
        });
        btnNewPublication.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NewPublicationActivity.class);
            lNewPublication.launch(intent);
        });


        btnLogout.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
