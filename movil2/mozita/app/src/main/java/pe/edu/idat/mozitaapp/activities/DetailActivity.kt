package pe.edu.idat.mozitaapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import pe.edu.idat.mozitaapp.R

class DetailActivity : AppCompatActivity() {

    private lateinit var fotoMascota: ImageView
    private lateinit var nombreMascota: TextView
    private lateinit var sexo: TextView
    private lateinit var edad: TextView
    private lateinit var categoria: TextView
    private lateinit var color: TextView
    private lateinit var descripcion: TextView
    private lateinit var contactar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Vistas
        fotoMascota = findViewById(R.id.fotoMascota)
        nombreMascota = findViewById(R.id.nombreMascota)
        sexo = findViewById(R.id.sexo)
        edad = findViewById(R.id.edad)
        categoria = findViewById(R.id.categoria)
        color = findViewById(R.id.color)
        descripcion = findViewById(R.id.descripcion)
        contactar = findViewById(R.id.contactar)

        // Obtener datos del Intent
        val nombre = intent.getStringExtra("nombre")
        val desc = intent.getStringExtra("descripcion")
        val url = intent.getStringExtra("imagenUrl")

        val sexoTxt = intent.getStringExtra("sexo")
        val edadTxt = intent.getStringExtra("edad")
        val catTxt = intent.getStringExtra("categoria")
        val colorTxt = intent.getStringExtra("color")

        // Validar
        if (nombre == null) {
            Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        llenarPantalla(nombre, desc, url, sexoTxt, edadTxt, catTxt, colorTxt)

        val duenoId = intent.getStringExtra("duenoId")

        // Botón contactar
        contactar.setOnClickListener {
            if (duenoId != null) {
                val intent = Intent(this, PersonActivity::class.java)
                // Pasamos el ID real para que PersonActivity haga la consulta
                intent.putExtra("DUENO_ID", duenoId)
                startActivity(intent)
            } else {
                Toast.makeText(this, "ID de dueño no disponible", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun llenarPantalla(
        n: String,
        d: String?,
        u: String?,
        s: String?,
        e: String?,
        ct: String?,
        cl: String?
    ) {

        nombreMascota.text = n
        descripcion.text = d ?: "Sin descripción"

        sexo.text = "Sexo: ${s ?: "N/A"}"
        edad.text = "Edad: ${e ?: "N/A"}"
        categoria.text = "Categoría: ${ct ?: "N/A"}"
        color.text = "Color: ${cl ?: "N/A"}"

        // Lógica de imagen local
        val categoriaTxt = ct?.lowercase() ?: "otros"

        val resourceId = when (categoriaTxt) {
            "perro" -> R.mipmap.perro
            "gato" -> R.mipmap.gato
            else -> R.mipmap.otros
        }

        // Se asigna el recurso directamente
        fotoMascota.setImageResource(resourceId)
    }
}