package viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import models.Mascota
import repository.MascotaRepository

// Cambiamos ViewModel() por AndroidViewModel(application)
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val mascotasLiveData = MutableLiveData<List<Mascota>>()
    private val mascotasOriginal: MutableList<Mascota> = ArrayList()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    // Inicializamos nuestro Repositorio pasándole el application (que actúa como contexto)
    private val mascotaRepository = MascotaRepository(application)

    init {
        //Llamamos primero a nuestra base de datos local
         cargarDatosLocales()


         cargarDatosDesdeFirestore()
    }

    // fun Leer desde SQLite
    private fun cargarDatosLocales() {
        try {
            // Le pedimos al repositorio que traiga la lista de mascotas
            val listaLocal = mascotaRepository.obtenerMascotas()

            mascotasOriginal.clear()
            mascotasOriginal.addAll(listaLocal)

            Log.d("SQLiteData", "Mascotas cargadas desde SQLite: ${mascotasOriginal.size}")

            // Actualizamos la UI para que el RecyclerView las pinte
            mascotasLiveData.postValue(ArrayList(mascotasOriginal))

        } catch (e: Exception) {
            Log.e("SQLiteError", "Error al cargar mascotas locales: ${e.message}")
        }
    }

    private fun cargarDatosDesdeFirestore() {

        // Escuchamos los cambios en Firebase en tiempo real
        db.collection("mascotas")
            .addSnapshotListener { value, error ->

                if (error != null) {
                    Log.e("FirestoreError", "Error al cargar mascotas: ${error.message}")
                    return@addSnapshotListener
                }

                if (value != null) {
                    // SINCRONIZACIÓN
                    for (doc: DocumentSnapshot in value.documents) {
                        val m = doc.toObject(Mascota::class.java)
                        if (m != null) {
                            m.setId(doc.id)

                            // Guardamos/Actualizamos la mascota de la nube en nuestro celular
                            mascotaRepository.insertar(m)
                        }
                    }

                    Log.d("Sync", "Sincronización con Firestore completada")

                    //  Volvemos a leer la base local para que la interfaz muestre los nuevos datos
                    cargarDatosLocales()
                }
            }
    }

    fun getMascotas(): LiveData<List<Mascota>> {
        return mascotasLiveData
    }

    fun filterBy(categoria: String) {

        if (categoria.equals("Todos", ignoreCase = true)) {
            mascotasLiveData.value = ArrayList(mascotasOriginal)

        } else {
            val filtradas: MutableList<Mascota> = ArrayList()

            for (m in mascotasOriginal) {

                // Evitar NullPointerException
                if (m.getCategoria() != null &&
                    m.getCategoria()!!.trim().equals(categoria.trim(), ignoreCase = true)
                ) {
                    filtradas.add(m)
                }
            }

            mascotasLiveData.value = filtradas
        }
    }
}