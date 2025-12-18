package com.example.masiwasimovil.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.masiwasimovil.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

import models.Mascota;

public class NewPublicationActivity extends AppCompatActivity {

    // Vistas
    private ImageView imgMascota; // En Java se llama así, lo asociaremos a imvPhoto del XML
    private Button btnOpenCamara, btnOpenGalery, btnGuardar; // Nuevos botones
    private EditText edtNombre, edtEdad, edtSexo, edtCategoria, edtColor, edtDescripcion;

    private Uri imageUri; // Para la imagen seleccionada (sea de cámara o galería)

    // Firebase
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;

    // --- 1. LANZADORES DE ACTIVIDAD (RESULT LAUNCHERS) ---

    // A) Lanzador para CÁMARA
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    if (extras != null) {
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        // Mostrar en el ImageView
                        imgMascota.setImageBitmap(imageBitmap);
                        // IMPORTANTE: Convertir Bitmap a Uri para que Firebase lo pueda subir
                        imageUri = getImageUri(this, imageBitmap);
                    }
                }
            }
    );

    // B) Lanzador para GALERÍA
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    imgMascota.setImageURI(imageUri);
                }
            }
    );

    // C) Lanzador de Permisos
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

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // --- 2. VINCULACIÓN DE VISTAS (MATCH ID JAVA <-> ID XML) ---

        // ¡OJO AQUÍ! Asociamos la variable imgMascota con el ID "imvPhoto" de tu XML nuevo
        imgMascota = findViewById(R.id.imvPhoto);

        // Botones nuevos del XML
        btnOpenCamara = findViewById(R.id.btnOpenCamara);
        btnOpenGalery = findViewById(R.id.btnOpenGalery);

        // Resto de campos (Igual que antes)
        btnGuardar = findViewById(R.id.btnGuardar);
        edtNombre = findViewById(R.id.edtNombre);
        edtEdad = findViewById(R.id.edtEdad);
        edtSexo = findViewById(R.id.edtSexo);
        edtCategoria = findViewById(R.id.edtCategoria);
        edtColor = findViewById(R.id.edtColor);
        edtDescripcion = findViewById(R.id.edtDescripcion);


        // --- 3. EVENTOS DE LOS BOTONES ---

        // Botón CÁMARA
        btnOpenCamara.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            } else {
                abrirCamara();
            }
        });

        // Botón GALERÍA
        btnOpenGalery.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });

        // Botón GUARDAR (Tu lógica original intacta)
        btnGuardar.setOnClickListener(v -> guardarEnFirebase());
    }

    // --- MÉTODOS AUXILIARES ---

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(intent);
        } else {
            Toast.makeText(this, "No se encontró app de cámara", Toast.LENGTH_SHORT).show();
        }
    }

    // Vital: Convierte la foto de la cámara en un archivo temporal para obtener una Uri
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "MasiWasiTemp_" + System.currentTimeMillis(), null);
        return Uri.parse(path);
    }

    // --- LÓGICA DE FIREBASE (INTACTA) ---

    private void guardarEnFirebase() {
        String nombre = edtNombre.getText().toString().trim();
        String edad = edtEdad.getText().toString().trim();

        if (nombre.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Nombre e imagen son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Subir Imagen a Storage
        StorageReference folder = storage.getReference().child("fotos_mascotas");
        StorageReference fileName = folder.child("img_" + System.currentTimeMillis());

        fileName.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            fileName.getDownloadUrl().addOnSuccessListener(uri -> {
                // Una vez que tenemos la URL, guardamos en Firestore
                subirDatos(nombre, edad, uri.toString());
            });
        }).addOnFailureListener(e -> Toast.makeText(this, "Error al subir imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void subirDatos(String nombre, String edad, String urlImagen) {
        if (mAuth.getCurrentUser() == null) return;

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

        db.collection("mascotas").add(mascota)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Publicación exitosa", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar datos", Toast.LENGTH_SHORT).show());
    }
}