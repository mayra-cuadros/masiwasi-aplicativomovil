package viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import models.Mascota

class HomeViewModel : ViewModel() {

    private val mascotasLiveData = MutableLiveData<List<Mascota>>()
    private val mascotasOriginal: MutableList<Mascota> = ArrayList()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        cargarDatosDesdeFirestore()
    }

    private fun cargarDatosDesdeFirestore() {
        // Usamos SnapshotListener para tiempo real
        db.collection("mascotas")
            .addSnapshotListener { value, error ->

                if (error != null) {
                    Log.e("FirestoreError", "Error al cargar mascotas: ${error.message}")
                    return@addSnapshotListener
                }

                if (value != null) {
                    mascotasOriginal.clear()

                    for (doc: DocumentSnapshot in value.documents) {
                        val m = doc.toObject(Mascota::class.java)
                        if (m != null) {
                            m.setId(doc.id)
                            mascotasOriginal.add(m)
                        }
                    }

                    Log.d("FirestoreData", "Mascotas cargadas: ${mascotasOriginal.size}")

                    // Actualizar UI
                    mascotasLiveData.postValue(ArrayList(mascotasOriginal))
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