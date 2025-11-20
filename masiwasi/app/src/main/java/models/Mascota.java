package models;

import java.io.Serializable;

public class Mascota implements Serializable {

    private String id;
    private String nombre;
    private String edad;
    private String sexo;
    private String descripcion;
    private String categoria;
    private String color;
    private String imageUrl;

    public Mascota() {
        // Constructor vacío requerido para Firestore
    }

    public Mascota(String id, String nombre, String edad, String sexo,
                   String descripcion, String categoria, String color,
                   String imageUrl) {

        this.id = id;
        this.nombre = nombre;
        this.edad = edad;
        this.sexo = sexo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.color = color;
        this.imageUrl = imageUrl;
    }

    // Getters y setters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEdad() { return edad; }
    public void setEdad(String edad) { this.edad = edad; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
