package pe.edu.idat.mozitaapp.fragments

import adapters.MascotaAdapter
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import models.Mascota
import models.Usuario
import pe.edu.idat.mozitaapp.R
import pe.edu.idat.mozitaapp.activities.DetailActivity
import pe.edu.idat.mozitaapp.activities.LoginActivity
import pe.edu.idat.mozitaapp.activities.NewPublicationActivity
import pe.edu.idat.mozitaapp.activities.RegisterActivity
import pe.edu.idat.mozitaapp.activities.UserProfileActivity
import repository.MascotaRepository
import repository.UsuarioRepository

class ProfileFragment : Fragment() {

    private lateinit var txtUserName: TextView
    private lateinit var txtUserEmail: TextView
    private lateinit var txtUserLocation: TextView
    private lateinit var btnEditProfile: Button
    private lateinit var btnNewPublication: Button
    private lateinit var btnLogout: Button
    private lateinit var rvUserPets: RecyclerView

    private lateinit var adapter: MascotaAdapter
    private var modoEdicion = false

    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    // Repositorios locales
    private lateinit var usuarioRepository: UsuarioRepository
    private lateinit var mascotaRepository: MascotaRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Firebase
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        // Inicializamos repositorios
        usuarioRepository = UsuarioRepository(requireContext())
        mascotaRepository = MascotaRepository(requireContext())

        // Vistas
        txtUserName = view.findViewById(R.id.txtUserName)
        txtUserEmail = view.findViewById(R.id.txtUserEmail)
        txtUserLocation = view.findViewById(R.id.txtUserLocation)
        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        btnNewPublication = view.findViewById(R.id.btnNewPublication)
        btnLogout = view.findViewById(R.id.btnLogout)
        rvUserPets = view.findViewById(R.id.rvUserPets)

        rvUserPets.layoutManager = LinearLayoutManager(requireContext())

        // Adapter CORRECTO con interfaz
        adapter = MascotaAdapter(
            requireContext(),
            mutableListOf(),
            modoEdicion,
            object : MascotaAdapter.OnMascotaClickListener {

                override fun onVerDetalles(mascota: Mascota) {

                    if (modoEdicion) {
                        val intent = Intent(requireContext(), NewPublicationActivity::class.java)
                        intent.putExtra("mascota_id", mascota.getId())
                        startActivity(intent)

                    } else {
                        val intent = Intent(requireContext(), DetailActivity::class.java)
                        intent.putExtra("nombre", mascota.getNombre())
                        intent.putExtra("descripcion", mascota.getDescripcion())
                        intent.putExtra("imagenUrl", mascota.getImageUrl())
                        intent.putExtra("sexo", mascota.getSexo())
                        intent.putExtra("edad", mascota.getEdad())
                        intent.putExtra("categoria", mascota.getCategoria())
                        intent.putExtra("color", mascota.getColor())
                        startActivity(intent)
                    }
                }
            }
        )

        rvUserPets.adapter = adapter

        // 🔥 VALIDAR USUARIO
        if (mAuth.currentUser != null) {

            cargarDatosUsuario()
            escucharPublicacionesMias()

            btnLogout.text = "Cerrar Sesión"
            btnLogout.setOnClickListener {
                mAuth.signOut()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

            //  Tocar el nombre para ir a Editar el Perfil
            txtUserName.setOnClickListener {
                startActivity(Intent(requireContext(), UserProfileActivity::class.java))
            }

        } else {

            txtUserName.text = "Invitado"
            txtUserEmail.text = "Inicia sesión para ver tu perfil"

            btnNewPublication.visibility = View.GONE
            btnEditProfile.visibility = View.GONE

            btnLogout.text = "Registrarme"
            btnLogout.setOnClickListener {
                startActivity(Intent(requireContext(), RegisterActivity::class.java))
            }
        }

        // Botones
        btnNewPublication.setOnClickListener {
            startActivity(Intent(requireContext(), NewPublicationActivity::class.java))
        }

        btnEditProfile.setOnClickListener {
            modoEdicion = !modoEdicion
            adapter.setModoEdicion(modoEdicion)
            btnEditProfile.text = if (modoEdicion) "Finalizar" else "Editar Adopción"
        }

        return view
    }

    private fun cargarDatosUsuario() {
        val userId = mAuth.currentUser?.uid ?: return

        db.collection("usuarios").document(userId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val nombre = doc.getString("nombre") ?: ""
                    val correo = doc.getString("correo") ?: ""
                    val direccion = doc.getString("direccion") ?: ""

                    txtUserName.text = nombre
                    txtUserEmail.text = correo
                    txtUserLocation.text = direccion

                    // 🌟 Guardamos/Actualizamos el perfil en SQLite para uso offline
                    val usuarioAct = Usuario(userId, nombre, correo, direccion, "", "", "", null)
                    usuarioRepository.insertar(usuarioAct)
                }
            }
    }

    private fun escucharPublicacionesMias() {
        val userId = mAuth.currentUser?.uid ?: return

        // Cargardesde SQLite las mascotas de este dueño
        try {
            val listaLocal = mascotaRepository.obtenerMascotas().filter { it.getDuenoId() == userId }
            adapter.updateList(listaLocal.toMutableList())
        } catch (e: Exception) { }

        // Actualizar desde Firebase y guardar en local
        db.collection("mascotas")
            .whereEqualTo("duenoId", userId)
            .addSnapshotListener { value, _ ->
                val lista = mutableListOf<Mascota>()
                value?.documents?.forEach { doc: DocumentSnapshot ->
                    val mascota = doc.toObject(Mascota::class.java)
                    mascota?.let {
                        it.setId(doc.id)
                        lista.add(it)
                        mascotaRepository.insertar(it) // Sincroniza SQLite
                    }
                }
                adapter.updateList(lista)
            }
    }
}