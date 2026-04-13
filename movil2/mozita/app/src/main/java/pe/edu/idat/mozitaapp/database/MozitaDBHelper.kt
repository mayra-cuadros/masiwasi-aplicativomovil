package pe.edu.idat.mozitaapp.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import models.Mascota
import models.Usuario

class MozitaDBHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "mozita.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_USUARIO = "usuario"
        const val TABLE_MASCOTA = "mascota"
    }

    override fun onCreate(db: SQLiteDatabase) {

        val createTableUsuario = """
            CREATE TABLE $TABLE_USUARIO (
                id TEXT PRIMARY KEY,
                nombre TEXT,
                email TEXT,
                direccion TEXT,
                telefono TEXT,
                location TEXT,
                imageUrl TEXT
            )
        """.trimIndent()

        val createTableMascota = """
            CREATE TABLE $TABLE_MASCOTA (
                id TEXT PRIMARY KEY,
                nombre TEXT,
                edad TEXT,
                sexo TEXT,
                descripcion TEXT,
                categoria TEXT,
                color TEXT,
                imageUrl TEXT,
                duenoId TEXT
            )
        """.trimIndent()

        db.execSQL(createTableUsuario)
        db.execSQL(createTableMascota)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MASCOTA")
        onCreate(db)
    }

    // --- MÉTODOS PARA USUARIO ---
    fun insertarUsuario(usuario: Usuario): Long {
        val db = this.writableDatabase
        val values = android.content.ContentValues().apply {
            put("id", usuario.getId())
            put("nombre", usuario.getNombre())
            put("email", usuario.email)
            put("direccion", usuario.getDireccion())
            put("telefono", usuario.getTelefono())
            put("location", usuario.getLocation())
            put("imageUrl", usuario.getImageUrl())
        }

        return db.insertWithOnConflict(TABLE_USUARIO, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun actualizarUsuario(usuario: Usuario): Int {
        val db = this.writableDatabase
        val values = android.content.ContentValues().apply {
            put("nombre", usuario.getNombre())
            put("direccion", usuario.getDireccion())
            put("telefono", usuario.getTelefono())
            put("imageUrl", usuario.getImageUrl())
        }
        return db.update(TABLE_USUARIO, values, "id=?", arrayOf(usuario.getId()))
    }

    fun eliminarUsuario(idUsuario: String): Int {
        val db = this.writableDatabase
        // Eliminamos el registro donde el 'id' coincida
        return db.delete(TABLE_USUARIO, "id=?", arrayOf(idUsuario))
    }

    // --- MÉTODOS PARA MASCOTA ---
    fun insertarMascota(mascota: Mascota): Long {
        val db = this.writableDatabase
        val values = android.content.ContentValues().apply {
            put("id", mascota.getId())
            put("nombre", mascota.getNombre())
            put("edad", mascota.getEdad())
            put("sexo", mascota.getSexo())
            put("descripcion", mascota.getDescripcion())
            put("categoria", mascota.getCategoria())
            put("color", mascota.getColor())
            put("imageUrl", mascota.getImageUrl())
            put("duenoId", mascota.getDuenoId())
        }

        return db.insertWithOnConflict(TABLE_MASCOTA, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }


    // --- MÉTODO PARA LEER MASCOTAS ---
    fun obtenerTodasLasMascotas(): List<Mascota> {
        val listaMascotas = mutableListOf<Mascota>()
        val db = this.readableDatabase

        // Hacemos la consulta a la tabla
        val cursor = db.rawQuery("SELECT * FROM $TABLE_MASCOTA", null)

        if (cursor.moveToFirst()) {
            do {
                val mascota = Mascota(
                    id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    edad = cursor.getString(cursor.getColumnIndexOrThrow("edad")),
                    sexo = cursor.getString(cursor.getColumnIndexOrThrow("sexo")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria")),
                    color = cursor.getString(cursor.getColumnIndexOrThrow("color")),
                    imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("imageUrl")),
                    duenoId = cursor.getString(cursor.getColumnIndexOrThrow("duenoId"))
                )
                listaMascotas.add(mascota)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return listaMascotas
    }



}