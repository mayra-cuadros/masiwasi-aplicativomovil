package com.example.masiwasimovil.fragments;

import android.content.Intent;
import android.os.Bundle;
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

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public ProfileFragment() {}

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Referenciar Vistas
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

        adapter = new MascotaAdapter(getContext(), mascotasList, modoEdicion, mascota -> {
            if (modoEdicion) {
                // Ir a editar (Firebase)
                Intent intent = new Intent(getContext(), NewPublicationActivity.class);
                intent.putExtra("mascota_id", mascota.getId());
                startActivity(intent);
            } else {
                // Ver detalles
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("EXTRA_MASCOTA", mascota);
                startActivity(intent);
            }
        });
        rvUserPets.setAdapter(adapter);

        // Cargar datos del usuario logueado
        if (mAuth.getCurrentUser() != null) {
            txtUserEmail.setText(mAuth.getCurrentUser().getEmail());
            txtUserName.setText(mAuth.getCurrentUser().getDisplayName() != null ? mAuth.getCurrentUser().getDisplayName() : "Usuario");
            escucharPublicaciones();
        }

        // Botón Nueva Publicación
        btnNewPublication.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), NewPublicationActivity.class));
        });

        // Botón Modo Edición
        btnEditProfile.setOnClickListener(v -> {
            modoEdicion = !modoEdicion;
            adapter.setModoEdicion(modoEdicion);
            btnEditProfile.setText(modoEdicion ? "Finalizar" : "Editar Publicaciones");
        });

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            getActivity().finish();
        });

        return view;
    }

    private void escucharPublicaciones() {
        String userId = mAuth.getCurrentUser().getUid();

        //Escucha cambios en tiempo real en Firestore
        db.collection("publicaciones")
                .whereEqualTo("dueñoId", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        mascotasList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Mascota mascota = doc.toObject(Mascota.class);
                            if (mascota != null) {
                                mascota.setId(doc.getId()); // Guardamos el ID del documento
                                mascotasList.add(mascota);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
