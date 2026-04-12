package repository

import android.content.Context
import models.Usuario
import pe.edu.idat.mozitaapp.database.MozitaDBHelper

class UsuarioRepository(context: Context) {

    // Instanciamos
    private val dbHelper = MozitaDBHelper(context)

    // guardar

    fun insertar(usuario: Usuario): Long {
        return dbHelper.insertarUsuario(usuario)
    }
}