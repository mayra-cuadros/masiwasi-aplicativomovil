package models

import java.io.Serializable

class Mascota : Serializable {

    private var id: String? = null
    private var nombre: String? = null
    private var edad: String? = null
    private var sexo: String? = null
    private var descripcion: String? = null
    private var categoria: String? = null
    private var color: String? = null
    private var imageUrl: String? = null
    private var duenoId: String? = null

    // Constructor vacío requerido para Firestore
    constructor()

    constructor(
        id: String?,
        nombre: String?,
        edad: String?,
        sexo: String?,
        descripcion: String?,
        categoria: String?,
        color: String?,
        imageUrl: String?,
        duenoId: String?
    ) {
        this.id = id
        this.nombre = nombre
        this.edad = edad
        this.sexo = sexo
        this.descripcion = descripcion
        this.categoria = categoria
        this.color = color
        this.imageUrl = imageUrl
        this.duenoId = duenoId
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

    fun getEdad(): String? {
        return edad
    }

    fun setEdad(edad: String?) {
        this.edad = edad
    }

    fun getSexo(): String? {
        return sexo
    }

    fun setSexo(sexo: String?) {
        this.sexo = sexo
    }

    fun getDescripcion(): String? {
        return descripcion
    }

    fun setDescripcion(descripcion: String?) {
        this.descripcion = descripcion
    }

    fun getCategoria(): String? {
        return categoria
    }

    fun setCategoria(categoria: String?) {
        this.categoria = categoria
    }

    fun getColor(): String? {
        return color
    }

    fun setColor(color: String?) {
        this.color = color
    }

    fun getImageUrl(): String? {
        return imageUrl
    }

    fun setImageUrl(imageUrl: String?) {
        this.imageUrl = imageUrl
    }

    fun getDuenoId(): String? {
        return duenoId
    }

    fun setDuenoId(duenoId: String?) {
        this.duenoId = duenoId
    }
}