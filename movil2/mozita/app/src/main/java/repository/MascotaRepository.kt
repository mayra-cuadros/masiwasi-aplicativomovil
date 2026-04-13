package repository

import android.content.Context
import models.Mascota
import pe.edu.idat.mozitaapp.database.MozitaDBHelper

class MascotaRepository(context: Context) {

    // Instanciamos BBDD Helper
    private val dbHelper = MozitaDBHelper(context)

    // guardar
    fun insertar(mascota: Mascota): Long {
        return dbHelper.insertarMascota(mascota)
    }

    //Listar

    fun obtenerMascotas(): List<Mascota> {
        return dbHelper.obtenerTodasLasMascotas()
    }
}