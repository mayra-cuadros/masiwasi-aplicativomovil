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
    private ImageView imgMascota; 
    private Button btnOpenCamara, btnOpenGalery, btnGuardar;
    private EditText edtNombre, edtEdad, edtSexo, edtCategoria, edtColor, edtDescripcion;

    private Uri imageUri;

    // Firebase
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
                        // Mostrar en el ImageView
                        imgMascota.setImageBitmap(imageBitmap);
                        // IMPORTANTE: Convertir Bitmap a Uri para que Firebase lo pueda subir
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


        imgMascota = findViewById(R.id.imvPhoto);


        btnOpenCamara = findViewById(R.id.btnOpenCamara);
        btnOpenGalery = findViewById(R.id.btnOpenGalery);


        btnGuardar = findViewById(R.id.btnGuardar);
        edtNombre = findViewById(R.id.edtNombre);
        edtEdad = findViewById(R.id.edtEdad);
        edtSexo = findViewById(R.id.edtSexo);
        edtCategoria = findViewById(R.id.edtCategoria);
        edtColor = findViewById(R.id.edtColor);
        edtDescripcion = findViewById(R.id.edtDescripcion);


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


    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(intent);
        } else {
            Toast.makeText(this, "No se encontró app de cámara", Toast.LENGTH_SHORT).show();
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