package models;

import java.io.Serializable;

public class Mascota implements Serializable{
    private String nombre;
    private String edad;
    private String sexo;
    private String descripcion;
    private String categoria;
    private String color;
    private String imagen;

    // Constructor
    public Mascota(String nombre, String edad, String sexo,
                   String descripcion, String categoria, String color,
                   String imagen) {
        this.nombre = nombre;
        this.edad = edad;
        this.sexo = sexo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.color = color;
        this.imagen = imagen;
    }



    // Getters y Setters
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

    public String getImagen() { return imagen; }
    public void setImagenUrl(String imagen) { this.imagen = imagen; }


}
