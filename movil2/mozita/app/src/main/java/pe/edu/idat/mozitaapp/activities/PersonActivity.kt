package pe.edu.idat.mozitaapp.activities

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import models.Usuario
import pe.edu.idat.mozitaapp.R

class PersonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person)

        val duenoId = intent.getStringExtra("DUENO_ID")

        if (duenoId != null) {
            cargarDatosDelDueno(duenoId)
        }
    }

    private fun cargarDatosDelDueno(id: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("usuarios").document(id).get()
            .addOnSuccessListener { doc ->
                if (doc != null) {
                    // Uso de los métodos de Usuario
                    val user = doc.toObject(Usuario::class.java)

                    findViewById< TextView>(R.id.tvNombreDueno).text = "Nombre: ${user?.getNombre()}"
                    findViewById<TextView>(R.id.tvEmailDueno).text = "Email: ${user?.email}"
//                    findViewById<TextView>(R.id.tvTelefonoDueno).text = "Teléfono: ${user?.getTelefono()}"
                    findViewById<TextView>(R.id.tvUbicacionDueno).text = "Ubicación: ${user?.getDireccion()}"
                } else {
                    // Si es que el ID no se encontró
                    Toast.makeText(this, "No existe el usuario con ID: $id", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error de conexión o permisos", Toast.LENGTH_SHORT).show()
            }
    }
}