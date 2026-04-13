package pe.edu.idat.mozitaapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import models.Usuario
import pe.edu.idat.mozitaapp.R
import repository.UsuarioRepository

class UserProfileActivity : AppCompatActivity() {

    private lateinit var edtEditNombre: EditText
    private lateinit var edtEditDireccion: EditText
    private lateinit var btnGuardarPerfil: Button
    private lateinit var btnEliminarCuenta: Button

    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private lateinit var usuarioRepository: UsuarioRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById<View?>(R.id.main),
            OnApplyWindowInsetsListener { v: View?, insets: WindowInsetsCompat? ->
                val systemBars = insets!!.getInsets(WindowInsetsCompat.Type.systemBars())
                v!!.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            })

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        usuarioRepository = UsuarioRepository(this)


        edtEditNombre = findViewById(R.id.edtEditNombre)
        edtEditDireccion = findViewById(R.id.edtEditDireccion)
        btnGuardarPerfil = findViewById(R.id.btnGuardarPerfil)
        btnEliminarCuenta = findViewById(R.id.btnEliminarCuenta)

        cargarDatosActuales()

        btnGuardarPerfil.setOnClickListener { actualizarPerfil() }
        btnEliminarCuenta.setOnClickListener { confirmarEliminacion() }
    }

    private fun cargarDatosActuales() {
        val userId = mAuth.currentUser?.uid ?: return
        db.collection("usuarios").document(userId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    edtEditNombre.setText(doc.getString("nombre"))
                    edtEditDireccion.setText(doc.getString("direccion"))
                }
            }
    }

    private fun actualizarPerfil() {
        val nuevoNombre = edtEditNombre.text.toString().trim()
        val nuevaDireccion = edtEditDireccion.text.toString().trim()
        val user = mAuth.currentUser

        if (user != null && nuevoNombre.isNotEmpty()) {
            val userId = user.uid

            // 1. Actualiza Firebase
            val userMap = mapOf("nombre" to nuevoNombre, "direccion" to nuevaDireccion)
            db.collection("usuarios").document(userId).update(userMap)
                .addOnSuccessListener {

                    // 2. Actualiza SQLite local
                    val usuarioEditado =
                        Usuario(userId, nuevoNombre, user.email, nuevaDireccion, "", "", "", null)
                    usuarioRepository.actualizar(usuarioEditado)

                    Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                    finish()
                }
        } else {
            Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmarEliminacion() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Cuenta")
            .setMessage("¿Estás seguro de que deseas eliminar tu cuenta permanentemente?")
            .setPositiveButton("Sí, eliminar") { _, _ -> eliminarCuenta() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarCuenta() {
        val user = mAuth.currentUser
        if (user != null) {
            val userId = user.uid

            // 1. Eliminar de Firestore
            db.collection("usuarios").document(userId).delete()
                .addOnSuccessListener {

                    // 2. Eliminar cuenta de autenticación
                    user.delete().addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            // 3. Eliminar de SQLite
                            usuarioRepository.eliminar(userId)

                            Toast.makeText(this, "Cuenta eliminada", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    }
                }
        }
    }

}