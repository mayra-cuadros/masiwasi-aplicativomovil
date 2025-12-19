package com.example.masiwasimovil.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.masiwasimovil.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

import models.Mascota;

public class NewPublicationActivity extends AppCompatActivity {

    private ImageView imgMascota;
    private Button btnOpenCamara, btnOpenGalery, btnGuardar, btnEliminar;
    private EditText edtNombre, edtEdad, edtSexo, edtCategoria, edtColor, edtDescripcion;

    private Uri imageUri;
    private String mascotaId = null;
    private boolean esEdicion = false;
    private String urlImagenActual = null;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    if (extras != null) {
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        imgMascota.setImageBitmap(imageBitmap);
                        imageUri = getImageUri(this, imageBitmap);
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    imgMascota.setImageURI(imageUri);
                }
            }
    );

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    abrirCamara();
                } else {
                    Toast.makeText(this, "Permiso de cámara necesario", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_publication);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Inicializar vistas
        imgMascota = findViewById(R.id.imvPhoto);
        btnOpenCamara = findViewById(R.id.btnOpenCamara);
        btnOpenGalery = findViewById(R.id.btnOpenGalery);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnEliminar = findViewById(R.id.btnEliminar);
        edtNombre = findViewById(R.id.edtNombre);
        edtEdad = findViewById(R.id.edtEdad);
        edtSexo = findViewById(R.id.edtSexo);
        edtCategoria = findViewById(R.id.edtCategoria);
        edtColor = findViewById(R.id.edtColor);
        edtDescripcion = findViewById(R.id.edtDescripcion);

        // Para poder editar
        mascotaId = getIntent().getStringExtra("mascota_id");
        if (mascotaId != null) {
            esEdicion = true;
            btnGuardar.setText("Guardar Cambios");
            cargarDatosMascota();
        }

        if (esEdicion) {
            btnEliminar.setVisibility(View.VISIBLE); // Se muestra si se está editando
            btnEliminar.setOnClickListener(v -> confirmarEliminacion());
        }

        btnOpenCamara.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            } else {
                abrirCamara();
            }
        });

        btnOpenGalery.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });

        btnGuardar.setOnClickListener(v -> guardarEnFirebase());
    }

    private void cargarDatosMascota() {
        db.collection("mascotas").document(mascotaId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Mascota mascota = documentSnapshot.toObject(Mascota.class);
                        if (mascota != null) {
                            edtNombre.setText(mascota.getNombre());
                            edtEdad.setText(mascota.getEdad());
                            edtSexo.setText(mascota.getSexo());
                            edtCategoria.setText(mascota.getCategoria());
                            edtColor.setText(mascota.getColor());
                            edtDescripcion.setText(mascota.getDescripcion());
                            urlImagenActual = mascota.getImageUrl();

                            if (urlImagenActual != null && !urlImagenActual.isEmpty()) {
                                Glide.with(this).load(urlImagenActual).into(imgMascota);
                            }
                        }
                    }
                });
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(intent);
        } else {
            try {
                cameraLauncher.launch(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Error al abrir cámara", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "MasiWasiTemp_" + System.currentTimeMillis(), null);
        return Uri.parse(path);
    }

    private void guardarEnFirebase() {
        String nombre = edtNombre.getText().toString().trim();
        String edad = edtEdad.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        // Si hay una imagen nueva seleccionada, la subimos
        if (imageUri != null) {
            StorageReference folder = storage.getReference().child("fotos_mascotas");
            StorageReference fileName = folder.child("img_" + System.currentTimeMillis());

            fileName.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                fileName.getDownloadUrl().addOnSuccessListener(uri -> {
                    subirDatos(nombre, edad, uri.toString());
                });
            }).addOnFailureListener(e -> Toast.makeText(this, "Error al subir imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            // Si no hay imagen nueva, usamos la urlImagenActual (que puede ser la vieja o null)
            subirDatos(nombre, edad, urlImagenActual);
        }
    }

    private void subirDatos(String nombre, String edad, String urlImagen) {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        Mascota mascota = new Mascota();
        mascota.setNombre(nombre);
        mascota.setEdad(edad);
        mascota.setSexo(edtSexo.getText().toString());
        mascota.setCategoria(edtCategoria.getText().toString());
        mascota.setColor(edtColor.getText().toString());
        mascota.setDescripcion(edtDescripcion.getText().toString());
        mascota.setImageUrl(urlImagen);
        mascota.setDuenoId(userId);

        if (esEdicion) {
            // Actualizar documento
            db.collection("mascotas").document(mascotaId)
                    .set(mascota)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "¡Cambios guardados!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            // Crear nuevo documento
            db.collection("mascotas")
                    .add(mascota)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "¡Mascota publicada con éxito!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void confirmarEliminacion() {
        // Preguntar antes de borrar
        new android.app.AlertDialog.Builder(this)
                .setTitle("Eliminar publicación")
                .setMessage("¿Estás seguro de borrar esta publicación?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarDeFirebase())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarDeFirebase() {
        db.collection("mascotas").document(mascotaId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Publicación eliminada", Toast.LENGTH_SHORT).show();
                    finish(); // Regresa al perfil
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}