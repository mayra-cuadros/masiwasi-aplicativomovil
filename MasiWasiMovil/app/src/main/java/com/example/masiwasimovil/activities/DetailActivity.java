package com.example.masiwasimovil.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.masiwasimovil.R;

import models.Mascota;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_MASCOTA = "mascota";

    private ImageView fotoMascota;
    private TextView nombreMascota, sexo, edad, categoria, color, descripcion;
    private Button contactar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.masiwasimovil.R.layout.activity_detail);

        fotoMascota = findViewById(R.id.fotoMascota);
        nombreMascota = findViewById(R.id.nombreMascota);
        sexo = findViewById(R.id.sexo);
        edad = findViewById(R.id.edad);
        categoria = findViewById(R.id.categoria);
        color = findViewById(R.id.color);
        descripcion = findViewById(R.id.descripcion);
        contactar = findViewById(R.id.contactar);

        // Obtener objeto enviado
        Mascota mascota = (Mascota) getIntent().getSerializableExtra("mascota");

        if (mascota == null) {
            Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mostrarDatos(mascota);

        contactar.setOnClickListener(v ->

                Toast.makeText(this, "Se contactará al dueño pronto.", Toast.LENGTH_SHORT).show()
        );

        contactar.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity.this, MainActivity.class);
            intent.putExtra("navigateTo", "profile");
            startActivity(intent);
            finish();
        });

    }

    private void mostrarDatos(Mascota mascota) {

        // Nombre
        nombreMascota.setText(
                mascota.getNombre() != null ? mascota.getNombre() : "Sin nombre"
        );

        // Chips
        sexo.setText("Sexo: " + (mascota.getSexo() != null ? mascota.getSexo() : "No especificado"));
        edad.setText("Edad: " + (mascota.getEdad() != null ? mascota.getEdad() : "No especificado"));
        categoria.setText("Categoría: " + (mascota.getCategoria() != null ? mascota.getCategoria() : "No especificado"));
        color.setText("Color: " + (mascota.getColor() != null ? mascota.getColor() : "No especificado"));

        // Descripción
        descripcion.setText(
                mascota.getDescripcion() != null ? mascota.getDescripcion() : "Sin descripción"
        );

        // Imagen
        int imageResId = getResources().getIdentifier(
                mascota.getImageUrl(),
                "mipmap",
                getPackageName()
        );

        if (imageResId != 0) {
            fotoMascota.setImageResource(imageResId);
        } else {
            fotoMascota.setImageResource(R.mipmap.mascota1);
        }

    }
}