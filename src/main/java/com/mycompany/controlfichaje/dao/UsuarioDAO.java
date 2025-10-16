package com.mycompany.controlfichaje.dao;

import java.sql.*;
import java.util.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class UsuarioDAO {

    // Obtener todos los usuarios
    public List<Map<String, String>> obtenerTodos() {
        List<Map<String, String>> usuarios = new ArrayList<>();
    String sql = "SELECT usuario, apellido, correo, rol, descripcion FROM usuarios";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, String> usuario = new HashMap<>();
                usuario.put("usuario", rs.getString("usuario"));
                usuario.put("apellido", rs.getString("apellido"));
                usuario.put("correo", rs.getString("correo"));
                usuario.put("rol", rs.getString("rol"));
                usuario.put("descripcion", rs.getString("descripcion"));
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    // Eliminar usuario
    public boolean eliminarUsuario(String usuario) {
        if ("admin".equals(usuario)) {
            return false; // No permitir eliminar al admin
        }
        
        String sql = "DELETE FROM usuarios WHERE usuario = ? AND usuario != 'admin'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usuario);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Verificar si un usuario ya existe
    public boolean existeUsuario(String usuario) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE usuario = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usuario);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Verificar credenciales del usuario
    public boolean verificarCredenciales(String usuario, String password) {
        String sql = "SELECT password FROM usuarios WHERE usuario = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usuario);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("password").equals(password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Obtener información del usuario
    public static Usuario obtenerUsuario(String usuario) {
    String sql = "SELECT usuario, apellido, correo, password, rol, descripcion FROM usuarios WHERE usuario = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usuario);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Usuario(
                    rs.getString("usuario"),
                    rs.getString("apellido"),
                    rs.getString("correo"),
                    rs.getString("password"),
                    rs.getString("rol"),
                    rs.getString("descripcion")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Crear nuevo usuario
    public boolean crearUsuario(String usuario, String apellido, String password, String rol, String descripcion) {
        // Nuevo método con correo
        return crearUsuario(usuario, apellido, null, password, rol, descripcion);
    }

    // Nuevo método para crear usuario con correo
    public boolean crearUsuario(String usuario, String apellido, String correo, String password, String rol, String descripcion) {
        String sql = "INSERT INTO usuarios (usuario, apellido, correo, password, rol, descripcion) VALUES (?, ?, ?, ?, ?, ?)";
        
        try {
            System.out.println("Iniciando creación de usuario con datos:");
            System.out.println("Usuario: " + usuario);
            System.out.println("Apellido: " + apellido);
            System.out.println("Correo: " + correo);
            System.out.println("Rol: " + rol);
            System.out.println("Descripción: " + descripcion);
            System.out.println("SQL a ejecutar: " + sql);

            // Verificar que los campos requeridos no sean nulos
            if (usuario == null || password == null || rol == null) {
                System.err.println("Error: campos requeridos son nulos");
                return false;
            }

            // Verificar conexión a la base de datos
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Error: No se pudo establecer conexión con la base de datos");
                return false;
            }
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, usuario);
            pstmt.setString(2, apellido != null ? apellido : "");
            pstmt.setString(3, correo != null ? correo : "");
            pstmt.setString(4, password);
            pstmt.setString(5, rol);
            pstmt.setString(6, descripcion != null ? descripcion : "");
            
            int resultado = pstmt.executeUpdate();
            System.out.println("Filas afectadas por la inserción: " + resultado);
            return resultado > 0;
        } catch (SQLException e) {
            System.err.println("Error SQL al crear usuario:");
            System.err.println("Mensaje: " + e.getMessage());
            System.err.println("Código de error: " + e.getErrorCode());
            System.err.println("Estado SQL: " + e.getSQLState());
            System.err.println("SQL ejecutada: " + sql);
            System.err.println("Valores: usuario=" + usuario + ", apellido=" + apellido + ", correo=" + correo +
                             ", rol=" + rol + ", descripcion=" + descripcion);
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Error general al crear usuario:");
            System.err.println("Tipo de error: " + e.getClass().getName());
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Actualizar usuario (si password es null o vacía, no se modifica)
    public boolean actualizarUsuario(String usuario, String apellido, String correo, String password, String rol, String descripcion) {
        // Mantener compatibilidad: no cambia el nombre de usuario
        return actualizarUsuario(usuario, usuario, apellido, correo, password, rol, descripcion);
    }

    // Actualizar incluyendo cambio de nombre de usuario (PK)
    public boolean actualizarUsuario(String originalUsuario, String nuevoUsuario, String apellido, String correo, String password, String rol, String descripcion) {
        boolean actualizarPassword = password != null && !password.isEmpty();
        String sql = actualizarPassword
                ? "UPDATE usuarios SET usuario = ?, apellido = ?, correo = ?, password = ?, rol = ?, descripcion = ? WHERE usuario = ?"
                : "UPDATE usuarios SET usuario = ?, apellido = ?, correo = ?, rol = ?, descripcion = ? WHERE usuario = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int index = 1;
            pstmt.setString(index++, nuevoUsuario);
            pstmt.setString(index++, apellido != null ? apellido : "");
            pstmt.setString(index++, correo != null ? correo : "");
            if (actualizarPassword) {
                pstmt.setString(index++, password);
            }
            pstmt.setString(index++, rol);
            pstmt.setString(index++, descripcion != null ? descripcion : "");
            pstmt.setString(index, originalUsuario);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obtener rol y descripcion (compatibilidad con Autenticacion)
    public Map<String, String> obtenerInfoUsuario(String usuario) {
        Map<String, String> info = new HashMap<>();
        String sql = "SELECT rol, descripcion FROM usuarios WHERE usuario = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    info.put("rol", rs.getString("rol"));
                    info.put("descripcion", rs.getString("descripcion"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return info;
    }
}