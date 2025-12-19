package com.example.masiwasimovil.fragments;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.masiwasimovil.R;
import com.example.masiwasimovil.activities.DetailActivity;
import com.example.masiwasimovil.activities.NewPublicationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import adapters.MascotaAdapter;
import models.Mascota;

import static android.app.Activity.RESULT_OK;

import viewmodels.ProfileViewModel; // Asegúrate de que el paquete sea correcto

public class ProfileFragment extends Fragment {

    private ImageView imgUserProfile;
    private TextView txtUserName, txtUserEmail, txtUserLocation;
    private Button btnEditProfile, btnNewPublication, btnLogout;
    private RecyclerView rvUserPets;

    private MascotaAdapter adapter;
    private boolean modoEdicion = false;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Inicializar vistas
        imgUserProfile = view.findViewById(R.id.imgUserProfile);
        txtUserName = view.findViewById(R.id.txtUserName);
        txtUserEmail = view.findViewById(R.id.txtUserEmail);
        txtUserLocation = view.findViewById(R.id.txtUserLocation);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnNewPublication = view.findViewById(R.id.btnNewPublication);
        btnLogout = view.findViewById(R.id.btnLogout);
        rvUserPets = view.findViewById(R.id.rvUserPets);

        rvUserPets.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MascotaAdapter(getContext(), new ArrayList<>(), modoEdicion, mascota -> {
            if (modoEdicion) {
                Intent intent = new Intent(getContext(), NewPublicationActivity.class);
                intent.putExtra("mascota_id", mascota.getId());
                startActivity(intent);
            } else {
                // Ver detalle
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("nombre", mascota.getNombre());
                intent.putExtra("descripcion", mascota.getDescripcion());
                intent.putExtra("imagenUrl", mascota.getImageUrl());
                intent.putExtra("sexo", mascota.getSexo());
                intent.putExtra("edad", mascota.getEdad());
                intent.putExtra("categoria", mascota.getCategoria());
                intent.putExtra("color", mascota.getColor());
                startActivity(intent);
            }
        });
        rvUserPets.setAdapter(adapter);

        // Cargar datos si el usuario está logueado
        if (mAuth.getCurrentUser() != null) {
            cargarDatosUsuario();
            escucharPublicacionesMias();
        } else {
            // Manejo para Invitados
            txtUserName.setText("Invitado");
            txtUserEmail.setText("Inicia sesión para ver tu perfil");
            btnNewPublication.setVisibility(View.GONE);
            btnEditProfile.setVisibility(View.GONE);
        }

        btnNewPublication.setOnClickListener(v -> startActivity(new Intent(getContext(), NewPublicationActivity.class)));

        btnEditProfile.setOnClickListener(v -> {
            modoEdicion = !modoEdicion;
            adapter.setModoEdicion(modoEdicion);
            btnEditProfile.setText(modoEdicion ? "Finalizar" : "Editar Adopción");
        });

        if (mAuth.getCurrentUser() != null) {
            // Si el usuario está logueado, el botón funciona para cerrar sesión
            btnLogout.setText("Cerrar Sesión");
            btnLogout.setOnClickListener(v -> {
                mAuth.signOut();
                // Al cerrar sesión, lo enviamos al Login para que pueda volver a entrar
                Intent intent = new Intent(getContext(), com.example.masiwasimovil.activities.LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        } else {
            // Si es invitado (mAuth.getCurrentUser() es null)
            btnLogout.setText("Registrarme");
            btnLogout.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));

            btnLogout.setOnClickListener(v -> {
                // Redirigir directamente al Registro
                Intent intent = new Intent(getContext(), com.example.masiwasimovil.activities.RegisterActivity.class);
                startActivity(intent);
            });
        }

        return view;
    }

    private void cargarDatosUsuario() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("usuarios").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        txtUserName.setText(documentSnapshot.getString("nombre"));
                        txtUserEmail.setText(documentSnapshot.getString("correo"));
                        txtUserLocation.setText(documentSnapshot.getString("direccion"));
                    }
                });
    }

    private void escucharPublicacionesMias() {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("mascotas")
                .whereEqualTo("duenoId", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        List<Mascota> misMascotas = new ArrayList<>();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Mascota m = doc.toObject(Mascota.class);
                            if (m != null) {
                                m.setId(doc.getId());
                                misMascotas.add(m);
                            }
                        }
                        adapter.updateList(misMascotas);
                    }
                });
    }
}