package com.example.masiwasimovil.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.masiwasimovil.R;

public class DetailActivity extends AppCompatActivity {

    private ImageView fotoMascota;
    private TextView nombreMascota, sexo, edad, categoria, color, descripcion;
    private Button contactar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // 1. Inicializar vistas
        fotoMascota = findViewById(R.id.fotoMascota);
        nombreMascota = findViewById(R.id.nombreMascota);
        sexo = findViewById(R.id.sexo);
        edad = findViewById(R.id.edad);
        categoria = findViewById(R.id.categoria);
        color = findViewById(R.id.color);
        descripcion = findViewById(R.id.descripcion);
        contactar = findViewById(R.id.contactar);

        // 2. Obtener los datos del Intent (deben coincidir con las llaves de HomeFragment)
        String nombre = getIntent().getStringExtra("nombre");
        String desc = getIntent().getStringExtra("descripcion");
        String url = getIntent().getStringExtra("imagenUrl");

        // Si pasaste más datos, recíbelos aquí también:
        String sexoTxt = getIntent().getStringExtra("sexo");
        String edadTxt = getIntent().getStringExtra("edad");
        String catTxt = getIntent().getStringExtra("categoria");
        String colorTxt = getIntent().getStringExtra("color");

        // 3. Validar y mostrar
        if (nombre == null) {
            Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        llenarPantalla(nombre, desc, url, sexoTxt, edadTxt, catTxt, colorTxt);

        // 4. Configurar botón contactar
        contactar.setOnClickListener(v -> {
            Toast.makeText(this, "Redirigiendo al perfil del dueño...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DetailActivity.this, MainActivity.class);
            intent.putExtra("navigateTo", "profile");
            startActivity(intent);
            finish();
        });
    }

    private void llenarPantalla(String n, String d, String u, String s, String e, String ct, String cl) {
        nombreMascota.setText(n);
        descripcion.setText(d != null ? d : "Sin descripción");

        // Llenar campos extra (con validación de nulos)
        sexo.setText("Sexo: " + (s != null ? s : "N/A"));
        edad.setText("Edad: " + (e != null ? e : "N/A"));
        categoria.setText("Categoría: " + (ct != null ? ct : "N/A"));
        color.setText("Color: " + (cl != null ? cl : "N/A"));

        // 5. CARGA DE IMAGEN REAL DESDE FIREBASE
        Glide.with(this)
                .load(u) // La URL que llega de Firestore
                .placeholder(R.drawable.ic_launcher_foreground) // Imagen temporal
                .error(R.mipmap.mascota1) // Si no hay foto, usa la de respaldo
                .into(fotoMascota);
    }
}