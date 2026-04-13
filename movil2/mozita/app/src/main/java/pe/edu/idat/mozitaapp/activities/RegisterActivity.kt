package pe.edu.idat.mozitaapp.activities

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import models.Usuario
import pe.edu.idat.mozitaapp.R
import repository.UsuarioRepository

class RegisterActivity : AppCompatActivity() {
    private lateinit var edtNombre: EditText
    private lateinit var edtCorreo: EditText
    private lateinit var edtPass: EditText
    private lateinit var edtPassConfirm: EditText
    private lateinit var edtDireccion: EditText
    private lateinit var btnRegistrar: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // Declaramos nuestro repositorio usuario
    private lateinit var usuarioRepository: UsuarioRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // Firebase
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        usuarioRepository = UsuarioRepository(this)

        // Vistas
        edtNombre = findViewById(R.id.editTextText)
        edtCorreo = findViewById(R.id.editTextTextEmailAddress2)
        edtPass = findViewById(R.id.editTextTextPassword2)
        edtPassConfirm = findViewById(R.id.editTextTextPassword3)
        edtDireccion = findViewById(R.id.editTextTextDireccion)
        btnRegistrar = findViewById(R.id.button3)

        btnRegistrar.setOnClickListener {
            registrarUsuario()
        }
    }

    private fun registrarUsuario() {
        val nombre = edtNombre.text.toString().trim()
        val correo = edtCorreo.text.toString().trim()
        val pass = edtPass.text.toString().trim()
        val pass2 = edtPassConfirm.text.toString().trim()
        val direccion = edtDireccion.text.toString().trim()

        // Validaciones
        if (nombre.isEmpty() || correo.isEmpty() || pass.isEmpty() || pass2.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (pass != pass2) {
            edtPassConfirm.error = "Las contraseñas no coinciden"
            return
        }

        // Crear usuario
        mAuth.createUserWithEmailAndPassword(correo, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val user: FirebaseUser? = mAuth.currentUser

                    user?.let {

                        val userId = it.uid

                        // 🔥 Actualizar nombre en Auth
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(nombre)
                            .build()

                        it.updateProfile(profileUpdates)

                        // SQLITE
                        // objeto Usuario
                        //

                        val nuevoUsuario = Usuario(
                            id = userId,
                            nombre = nombre,
                            email = correo,
                            direccion = direccion,
                            telefono = "",
                            location = "",
                            imageUrl = "",
                            mascotas = null
                        )

                        // Lo insertamos en la base de datos local SQLite
                        usuarioRepository.insertar(nuevoUsuario)

                        // 🔥 Guardar en Firestore
                        val userMap = hashMapOf(
                            "nombre" to nombre,
                            "correo" to correo,
                            "direccion" to direccion
                        )

                        db.collection("usuarios").document(userId).set(userMap)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }

                } else {
                    Toast.makeText(
                        this,
                        "Fallo: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}