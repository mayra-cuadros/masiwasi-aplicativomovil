package models;

import java.io.Serializable;
import java.util.List;

public class Usuario implements Serializable {

    private String id;
    private String nombre;
    private String email;
    private String direccion;
    private String telefono;
    private String location;
    private String imageUrl;
    private List<Mascota> mascotas;

    public Usuario() {
        // Constructor vacío requerido por Firestore
    }

    public Usuario(String id, String nombre, String email, String direccion,
                   String telefono, String location, String imageUrl,
                   List<Mascota> mascotas) {

        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.direccion = direccion;
        this.telefono = telefono;
        this.location = location;
        this.imageUrl = imageUrl;
        this.mascotas = mascotas;
    }

    // Getters y setters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public List<Mascota> getMascotas() { return mascotas; }
    public void setMascotas(List<Mascota> mascotas) { this.mascotas = mascotas; }
}
