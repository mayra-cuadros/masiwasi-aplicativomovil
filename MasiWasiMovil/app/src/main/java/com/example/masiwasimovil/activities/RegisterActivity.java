package com.example.masiwasimovil.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.masiwasimovil.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText edtNombre, edtCorreo, edtPass, edtPassConfirm, editTextTextDireccion;
    Button btnRegistrar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtNombre = findViewById(R.id.editTextText);
        edtCorreo = findViewById(R.id.editTextTextEmailAddress2);
        edtPass = findViewById(R.id.editTextTextPassword2);
        edtPassConfirm = findViewById(R.id.editTextTextPassword3);
        editTextTextDireccion = findViewById(R.id.editTextTextDireccion);
        btnRegistrar = findViewById(R.id.button3);

        btnRegistrar.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        String nombre = edtNombre.getText().toString().trim();
        String correo = edtCorreo.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();
        String pass2 = edtPassConfirm.getText().toString().trim();
        String direccion = editTextTextDireccion.getText().toString().trim();

        // 1. Validaciones de UI
        if (nombre.isEmpty() || correo.isEmpty() || pass.isEmpty() || pass2.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(pass2)) {
            edtPassConfirm.setError("Las contraseñas no coinciden");
            return;
        }

        // bloque para crear el usuario
        mAuth.createUserWithEmailAndPassword(correo, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();

                            // Actualizar nombre en el Perfil de Auth
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nombre)
                                    .build();
                            user.updateProfile(profileUpdates);

                            // Guardar TODOS los datos en Firestore
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("nombre", nombre);
                            userMap.put("correo", correo);
                            userMap.put("direccion", direccion);

                            db.collection("usuarios").document(userId).set(userMap)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                        finish(); // Cierra y vuelve al login
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Error al guardar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Error";
                        Toast.makeText(this, "Fallo de registro: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void actualizarNombreUsuario(FirebaseUser user, String nombre) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nombre)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Usuario creado con éxito", Toast.LENGTH_SHORT).show();
                        finish(); // Regresa al Login
                    }
                });
    }
}
