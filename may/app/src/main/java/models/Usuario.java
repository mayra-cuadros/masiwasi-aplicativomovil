package models;

import java.util.List;

public class Usuario {
    private String nombre;
    private String correo;
    private String direccion;
    private String telefono;
    private String localidad;
    private String imagen;
    private List<Mascota> mascotas;

    public Usuario(String nombre, String correo, String direccion, String telefono, String localidad, String imagen, List<Mascota> mascotas) {
        this.nombre = nombre;
        this.correo = correo;
        this.direccion = direccion;
        this.telefono = telefono;
        this.localidad = localidad;
        this.imagen = imagen;
        this.mascotas = mascotas;
    }

    // Getters y setters
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getDireccion() { return direccion; }
    public String getTelefono() { return telefono; }
    public String getLocalidad() { return localidad; }
    public String getImagen() { return imagen; }
    public List<Mascota> getMascotas() { return mascotas; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }
    public void setImagen(String imagenUrl) { this.imagen = imagenUrl; }
    public void setMascotas(List<Mascota> mascotas) { this.mascotas = mascotas; }
}
