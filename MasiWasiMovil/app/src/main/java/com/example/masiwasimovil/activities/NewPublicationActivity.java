package com.example.masiwasimovil.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.masiwasimovil.R;

public class NewPublicationActivity extends AppCompatActivity {

    private ImageView imgMascota;
    private Button btnChangeImage, btnGuardar;
    private EditText edtNombre, edtEdad, edtSexo, edtCategoria, edtColor, edtDescripcion;

    private boolean modoEdicion = false;
    private String mascotaId; // Para edición

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_publication);

        // Vistas
        imgMascota = findViewById(R.id.imgMascota);
        btnChangeImage = findViewById(R.id.btnChangeImage);
        btnGuardar = findViewById(R.id.btnGuardar);

        edtNombre = findViewById(R.id.edtNombre);
        edtEdad = findViewById(R.id.edtEdad);
        edtSexo = findViewById(R.id.edtSexo);
        edtCategoria = findViewById(R.id.edtCategoria);
        edtColor = findViewById(R.id.edtColor);
        edtDescripcion = findViewById(R.id.edtDescripcion);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("mascota_id")) {
            modoEdicion = true;
            mascotaId = intent.getStringExtra("mascota_id");
            edtNombre.setText(intent.getStringExtra("mascota_nombre"));
            edtEdad.setText(intent.getStringExtra("mascota_edad"));
            edtSexo.setText(intent.getStringExtra("mascota_sexo"));
            edtCategoria.setText(intent.getStringExtra("mascota_categoria"));
            edtColor.setText(intent.getStringExtra("mascota_color"));
            edtDescripcion.setText(intent.getStringExtra("mascota_descripcion"));
            imgMascota.setImageResource(R.mipmap.ic_launcher);
        }

        btnChangeImage.setOnClickListener(v -> {
            Toast.makeText(this, "Subir imagen pendiente", Toast.LENGTH_SHORT).show();
        });

        btnGuardar.setOnClickListener(v -> {
            String nombre = edtNombre.getText().toString().trim();
            String edad = edtEdad.getText().toString().trim();
            String sexo = edtSexo.getText().toString().trim();
            String categoria = edtCategoria.getText().toString().trim();
            String color = edtColor.getText().toString().trim();
            String descripcion = edtDescripcion.getText().toString().trim();

            if (nombre.isEmpty() || edad.isEmpty() || sexo.isEmpty() || categoria.isEmpty() || color.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("mascota_id", mascotaId != null ? mascotaId : String.valueOf(System.currentTimeMillis())); // id temporal
            resultIntent.putExtra("mascota_nombre", nombre);
            resultIntent.putExtra("mascota_edad", edad);
            resultIntent.putExtra("mascota_sexo", sexo);
            resultIntent.putExtra("mascota_categoria", categoria);
            resultIntent.putExtra("mascota_color", color);
            resultIntent.putExtra("mascota_descripcion", descripcion);
            resultIntent.putExtra("mascota_imageUrl", "");

            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
