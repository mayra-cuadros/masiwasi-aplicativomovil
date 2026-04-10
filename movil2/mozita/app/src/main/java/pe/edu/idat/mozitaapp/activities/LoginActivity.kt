package pe.edu.idat.mozitaapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pe.edu.idat.mozitaapp.R

// ✅ IMPORTS QUE FALTABAN
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    var email: EditText? = null
    var password: EditText? = null
    var btnLogin: Button? = null
    var btnRegister: Button? = null

    private var mAuth: FirebaseAuth? = null

    override fun onStart() {
        super.onStart()

        // ✅ CORREGIDO (Kotlin moderno)
        val currentUser: FirebaseUser? = mAuth?.currentUser
        if (currentUser != null) {
            irAMain()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        email = findViewById(R.id.editTextTextEmailAddress)
        password = findViewById(R.id.editTextTextPassword)
        btnLogin = findViewById(R.id.button)
        btnRegister = findViewById(R.id.button2)

        // ✅ CORREGIDO (evitar !!)
        btnLogin?.setOnClickListener { validarDatos() }

        btnRegister?.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validarDatos() {
        val correo = email?.text.toString().trim()
        val pass = password?.text.toString().trim()

        if (correo.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // ✅ CORREGIDO (safe call)
        mAuth?.signInWithEmailAndPassword(correo, pass)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()
                    irAMain()
                } else {
                    val errorMsg = task.exception?.message ?: "Error desconocido"
                    Toast.makeText(
                        this,
                        "Fallo de autenticación: $errorMsg",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun irAMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("openProfile", true)
        startActivity(intent)
        finish()
    }
}