package com.mycompany.controlfichaje.dao;

/**
 * Modelo de dominio para un usuario del sistema.
 *
 * Atributos:
 * - id: clave primaria numérica (autoincrement) en base de datos.
 * - usuario: nombre visible del usuario (alias mostrable en la UI).
 * - apellido: apellidos del usuario.
 * - correo: identificador único para login; se usa para autenticación.
 * - password: contraseña (en BD está almacenada en texto plano en este proyecto,
 *   lo ideal sería usar hash+salt).
 * - rol: perfil de acceso (por ejemplo, "admin" o "usuario").
 * - descripcion: información adicional que se muestra en el perfil.
 */
public class Usuario {
    private int id;
    private String usuario;
    private String apellido;
    private String correo;
    private String password;
    private String rol;
    private String descripcion;

    public Usuario(int id, String usuario, String apellido, String correo, String password, String rol, String descripcion) {
        this.id = id;
        this.usuario = usuario;
        this.apellido = apellido;
        this.correo = correo;
        this.password = password;
        this.rol = rol;
        this.descripcion = descripcion;
    }

    // Getters
    public int getId() { return id; }
    public String getUsuario() { return usuario; }
    public String getApellido() { return apellido; }
    public String getCorreo() { return correo; }
    public String getPassword() { return password; }
    public String getRol() { return rol; }
    public String getDescripcion() { return descripcion; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setPassword(String password) { this.password = password; }
    public void setRol(String rol) { this.rol = rol; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}