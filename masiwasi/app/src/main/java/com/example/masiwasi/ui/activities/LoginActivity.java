package com.example.masiwasi.ui.activities;

import android.content.Intent;
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

import com.example.masiwasi.R;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        btnLogin = findViewById(R.id.button);
        btnRegister = findViewById(R.id.button2);

        btnLogin.setOnClickListener(v -> validarDatos());

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void validarDatos() {
        String correo = email.getText().toString().trim();
        String pass = password.getText().toString().trim();

        // --- ADMIN ---
        if (correo.equals("admin") && pass.equals("admin")) {
            Toast.makeText(this, "Inicio de sesión exitoso (ADMIN)", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, DetailActivity.class);
            startActivity(intent);
            finish();
            return;
        }


        // 1. Correo vacío
        if (correo.isEmpty()) {
            email.setError("Ingresa tu correo");
            email.requestFocus();
            return;
        }

        // 2. El correo NO debe tener espacios
        if (correo.contains(" ")) {
            email.setError("El correo no debe contener espacios");
            email.requestFocus();
            return;
        }

        // 3. Validar caracteres válidos del correo
        if (!correo.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            email.setError("El correo contiene caracteres inválidos");
            email.requestFocus();
            return;
        }

        // 4. Formato de correo válido (Android Patterns)
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            email.setError("Correo inválido");
            email.requestFocus();
            return;
        }

        // 5. Contraseña vacía
        if (pass.isEmpty()) {
            password.setError("Ingresa tu contraseña");
            password.requestFocus();
            return;
        }

        // 6. La contraseña no debe tener espacios
        if (pass.contains(" ")) {
            password.setError("La contraseña no debe contener espacios");
            password.requestFocus();
            return;
        }

        // === VALIDAR CONTRA USUARIO REGISTRADO ===
        SharedPreferences prefs = getSharedPreferences("USUARIO", MODE_PRIVATE);
        String correoGuardado = prefs.getString("correo", null);
        String passGuardada = prefs.getString("password", null);

        // 7. No existe usuario guardado
        if (correoGuardado == null) {
            Toast.makeText(this, "No existe un usuario registrado.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 8. Validar correo ignorando mayúsculas (equalsIgnoreCase)
        if (!correo.equalsIgnoreCase(correoGuardado)) {
            Toast.makeText(this, "El correo no coincide con el registrado", Toast.LENGTH_SHORT).show();
            return;
        }

        // 9. Validar contraseña incorrecta
        if (!pass.equals(passGuardada)) {
            Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
            return;
        }

        // === USUARIO CORRECTO ===
        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, DetailActivity.class);
        startActivity(intent);
        finish();
    }
}
