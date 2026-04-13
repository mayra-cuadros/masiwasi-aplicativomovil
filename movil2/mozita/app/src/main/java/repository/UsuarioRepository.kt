package repository

import android.content.Context
import models.Usuario
import pe.edu.idat.mozitaapp.database.MozitaDBHelper

class UsuarioRepository(context: Context) {

    // Instanciamos
    private val dbHelper = MozitaDBHelper(context)

    // guardar SQLite

    fun insertar(usuario: Usuario): Long {
        return dbHelper.insertarUsuario(usuario)
    }

    // actualizar SQLite
    fun actualizar(usuario: Usuario): Int {
        return dbHelper.actualizarUsuario(usuario)
    }

    // borrar  SQLite
    fun eliminar(idUsuario: String): Int {
        return dbHelper.eliminarUsuario(idUsuario)
    }
}