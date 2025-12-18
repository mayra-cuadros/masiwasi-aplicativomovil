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

public class RegisterActivity extends AppCompatActivity {
    EditText edtNombre, edtCorreo, edtPass, edtPassConfirm;
    Button btnRegistrar;

    private FirebaseAuth mAuth;

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtNombre = findViewById(R.id.editTextText);
        edtCorreo = findViewById(R.id.editTextTextEmailAddress2);
        edtPass = findViewById(R.id.editTextTextPassword2);
        edtPassConfirm = findViewById(R.id.editTextTextPassword3);
        btnRegistrar = findViewById(R.id.button3);

        btnRegistrar.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        String nombre = edtNombre.getText().toString().trim();
        String correo = edtCorreo.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();
        String pass2 = edtPassConfirm.getText().toString().trim();

        // Validaciones
        if (nombre.isEmpty() || correo.isEmpty() || pass.isEmpty() || pass2.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            edtCorreo.setError("Correo inválido");
            return;
        }

        if (pass.length() < 6) {
            edtPass.setError("La contraseña debe tener al menos 6 caracteres");
            return;
        }

        if (!pass.equals(pass2)) {
            edtPassConfirm.setError("Las contraseñas no coinciden");
            return;
        }

        // REAR USUARIO EN FIREBASE AUTHENTICATION
        mAuth.createUserWithEmailAndPassword(correo, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // GUARDAR EL NOMBRE EN EL PERFIL DE FIREBASE
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            actualizarNombreUsuario(user, nombre);
                        }
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Error";
                        Toast.makeText(this, "Error al registrar: " + error, Toast.LENGTH_LONG).show();
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
