package com.example.masiwasimovil.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.masiwasimovil.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import models.Mascota;

public class NewPublicationActivity extends AppCompatActivity {

    private ImageView imgMascota;
    private Button btnChangeImage, btnGuardar;
    private EditText edtNombre, edtEdad, edtSexo, edtCategoria, edtColor, edtDescripcion;

    private Uri imageUri; // Para la imagen seleccionada
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_publication);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Referenciar Vistas
        imgMascota = findViewById(R.id.imgMascota);
        btnChangeImage = findViewById(R.id.btnChangeImage);
        btnGuardar = findViewById(R.id.btnGuardar);
        edtNombre = findViewById(R.id.edtNombre);
        edtEdad = findViewById(R.id.edtEdad);
        edtSexo = findViewById(R.id.edtSexo);
        edtCategoria = findViewById(R.id.edtCategoria);
        edtColor = findViewById(R.id.edtColor);
        edtDescripcion = findViewById(R.id.edtDescripcion);

        // Botón para seleccionar imagen de la galería
        btnChangeImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        });

        btnGuardar.setOnClickListener(v -> guardarEnFirebase());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imgMascota.setImageURI(imageUri);
        }
    }

    private void guardarEnFirebase() {
        String nombre = edtNombre.getText().toString().trim();
        String edad = edtEdad.getText().toString().trim();

        if (nombre.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Nombre e imagen son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Subir Imagen a Storage
        StorageReference folder = storage.getReference().child("fotos_mascotas");
        StorageReference fileName = folder.child("img_" + System.currentTimeMillis());

        fileName.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            fileName.getDownloadUrl().addOnSuccessListener(uri -> {
                // 2. Una vez que tenemos la URL, guardamos en Firestore
                subirDatos(nombre, edad, uri.toString());
            });
        }).addOnFailureListener(e -> Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show());
    }

    private void subirDatos(String nombre, String edad, String urlImagen) {
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
                    finish(); // Regresa al perfil
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar datos", Toast.LENGTH_SHORT).show());
    }
}
