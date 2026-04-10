package pe.edu.idat.mozitaapp.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.*
import models.Mascota
import pe.edu.idat.mozitaapp.R
import java.io.ByteArrayOutputStream
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class NewPublicationActivity : AppCompatActivity() {

    private var imgMascota: ImageView? = null
    private var btnOpenCamara: Button? = null
    private var btnOpenGalery: Button? = null
    private var btnGuardar: Button? = null
    private var btnEliminar: Button? = null
    private var edtNombre: EditText? = null
    private var edtEdad: EditText? = null
    private var edtSexo: EditText? = null
    private var edtCategoria: EditText? = null
    private var edtColor: EditText? = null
    private var edtDescripcion: EditText? = null

    private var imageUri: Uri? = null
    private var mascotaId: String? = null
    private var esEdicion = false
    private var urlImagenActual: String? = null

    private var db: FirebaseFirestore? = null
    private var storage: FirebaseStorage? = null
    private var mAuth: FirebaseAuth? = null

    private val cameraLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val extras = result.data!!.extras
            val imageBitmap = extras?.get("data") as Bitmap?
            imgMascota?.setImageBitmap(imageBitmap)
            imageUri = getImageUri(this, imageBitmap!!)
        }
    }

    private val galleryLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            imageUri = result.data!!.data
            imgMascota?.setImageURI(imageUri)
        }
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(RequestPermission()) { isGranted ->
        if (isGranted) {
            abrirCamara()
        } else {
            Toast.makeText(this, "Permiso de cámara necesario", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_publication)

        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        mAuth = FirebaseAuth.getInstance()

        imgMascota = findViewById(R.id.imvPhoto)
        btnOpenCamara = findViewById(R.id.btnOpenCamara)
        btnOpenGalery = findViewById(R.id.btnOpenGalery)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnEliminar = findViewById(R.id.btnEliminar)
        edtNombre = findViewById(R.id.edtNombre)
        edtEdad = findViewById(R.id.edtEdad)
        edtSexo = findViewById(R.id.edtSexo)
        edtCategoria = findViewById(R.id.edtCategoria)
        edtColor = findViewById(R.id.edtColor)
        edtDescripcion = findViewById(R.id.edtDescripcion)

        mascotaId = intent.getStringExtra("mascota_id")
        if (mascotaId != null) {
            esEdicion = true
            btnGuardar?.text = "Guardar Cambios"
            cargarDatosMascota()
        }

        if (esEdicion) {
            btnEliminar?.visibility = View.VISIBLE
            btnEliminar?.setOnClickListener { confirmarEliminacion() }
        }

        btnOpenCamara?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            } else {
                abrirCamara()
            }
        }

        btnOpenGalery?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(intent)
        }

        btnGuardar?.setOnClickListener { guardarEnFirebase() }
    }

    private fun cargarDatosMascota() {
        db?.collection("mascotas")?.document(mascotaId!!)?.get()
            ?.addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val mascota = doc.toObject(Mascota::class.java)
                    mascota?.let {
                        edtNombre?.setText(it.getNombre())
                        edtEdad?.setText(it.getEdad())
                        edtSexo?.setText(it.getSexo())
                        edtCategoria?.setText(it.getCategoria())
                        edtColor?.setText(it.getColor())
                        edtDescripcion?.setText(it.getDescripcion())
                        urlImagenActual = it.getImageUrl()

                        if (!urlImagenActual.isNullOrEmpty()) {
                            Glide.with(this).load(urlImagenActual).into(imgMascota!!)
                        }
                    }
                }
            }
    }

    private fun abrirCamara() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }

    fun getImageUri(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            context.contentResolver,
            bitmap,
            "Temp_" + System.currentTimeMillis(),
            null
        )
        return Uri.parse(path)
    }

    private fun guardarEnFirebase() {
        val nombre = edtNombre?.text.toString().trim()
        val edad = edtEdad?.text.toString().trim()

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUri != null) {
            val folder = storage?.reference?.child("fotos_mascotas")
            val fileName = folder?.child("img_" + System.currentTimeMillis())

            imageUri?.let {
                fileName?.putFile(it)
                    ?.addOnSuccessListener {
                        fileName.downloadUrl.addOnSuccessListener { uri ->
                            subirDatos(nombre, edad, uri.toString())
                        }
                    }
                    ?.addOnFailureListener {
                        Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            subirDatos(nombre, edad, urlImagenActual)
        }
    }

    private fun subirDatos(nombre: String?, edad: String?, urlImagen: String?) {
        if (mAuth?.currentUser == null) {
            Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = mAuth?.currentUser?.uid

        val mascota = Mascota()
        mascota.setNombre(nombre)
        mascota.setEdad(edad)
        mascota.setSexo(edtSexo?.text.toString())
        mascota.setCategoria(edtCategoria?.text.toString())
        mascota.setColor(edtColor?.text.toString())
        mascota.setDescripcion(edtDescripcion?.text.toString())
        mascota.setImageUrl(urlImagen)
        mascota.setDuenoId(userId)

        if (esEdicion) {
            db?.collection("mascotas")?.document(mascotaId!!)
                ?.set(mascota)
                ?.addOnSuccessListener {
                    Toast.makeText(this, "¡Cambios guardados!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                ?.addOnFailureListener {
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            db?.collection("mascotas")
                ?.add(mascota)
                ?.addOnSuccessListener {
                    Toast.makeText(this, "¡Mascota publicada!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                ?.addOnFailureListener {
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun confirmarEliminacion() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar")
            .setMessage("¿Seguro?")
            .setPositiveButton("Sí") { _, _ -> eliminarDeFirebase() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun eliminarDeFirebase() {
        db?.collection("mascotas")?.document(mascotaId!!)
            ?.delete()
            ?.addOnSuccessListener {
                Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show()
                finish()
            }
            ?.addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}