package models

import com.google.firebase.firestore.PropertyName
import java.io.Serializable

class Usuario : Serializable {

    private var id: String? = null
    private var nombre: String? = null
    @get:PropertyName("correo")
    @set:PropertyName("correo")
    var email: String? = null
    private var direccion: String? = null
    private var telefono: String? = null
    private var location: String? = null
    private var imageUrl: String? = null
    private var mascotas: List<Mascota>? = null

    // Constructor vacío requerido por Firestore
    constructor()

    constructor(
        id: String?,
        nombre: String?,
        email: String?,
        direccion: String?,
        telefono: String?,
        location: String?,
        imageUrl: String?,
        mascotas: List<Mascota>?
    ) {
        this.id = id
        this.nombre = nombre
        this.email = email
        this.direccion = direccion
        this.telefono = telefono
        this.location = location
        this.imageUrl = imageUrl
        this.mascotas = mascotas
    }

    // Getters y setters

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun getNombre(): String? {
        return nombre
    }

    fun setNombre(nombre: String?) {
        this.nombre = nombre
    }


    fun getDireccion(): String? {
        return direccion
    }

    fun setDireccion(direccion: String?) {
        this.direccion = direccion
    }

    fun getTelefono(): String? {
        return telefono
    }

    fun setTelefono(telefono: String?) {
        this.telefono = telefono
    }

    fun getLocation(): String? {
        return location
    }

    fun setLocation(location: String?) {
        this.location = location
    }

    fun getImageUrl(): String? {
        return imageUrl
    }

    fun setImageUrl(imageUrl: String?) {
        this.imageUrl = imageUrl
    }

    fun getMascotas(): List<Mascota>? {
        return mascotas
    }

    fun setMascotas(mascotas: List<Mascota>?) {
        this.mascotas = mascotas
    }
}