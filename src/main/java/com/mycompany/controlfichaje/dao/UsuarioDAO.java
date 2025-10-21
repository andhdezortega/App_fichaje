package com.mycompany.controlfichaje.dao;

import java.sql.*;
import java.util.*;

public class UsuarioDAO {

    // Métodos de UsuarioDAO

    // Actualizar usuario por id
    public boolean actualizarUsuarioPorId(int id, String usuario, String apellido, String correo, String password, String rol, String descripcion) {
        boolean actualizarPassword = password != null && !password.isEmpty();
        String sql = actualizarPassword
                ? "UPDATE usuarios SET usuario = ?, apellido = ?, correo = ?, password = ?, rol = ?, descripcion = ? WHERE id = ?"
                : "UPDATE usuarios SET usuario = ?, apellido = ?, correo = ?, rol = ?, descripcion = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int index = 1;
            pstmt.setString(index++, usuario);
            pstmt.setString(index++, apellido != null ? apellido : "");
            pstmt.setString(index++, correo != null ? correo : "");
            if (actualizarPassword) {
                pstmt.setString(index++, password);
            }
            pstmt.setString(index++, rol);
            pstmt.setString(index++, descripcion != null ? descripcion : "");
            pstmt.setInt(index, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obtener todos los usuarios
    public List<Map<String, String>> obtenerTodos() {
        List<Map<String, String>> usuarios = new ArrayList<>();
        String sql = "SELECT id, usuario, apellido, correo, rol, descripcion FROM usuarios";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, String> usuario = new HashMap<>();
                usuario.put("id", String.valueOf(rs.getInt("id")));
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

    // Eliminar usuario por id
    public boolean eliminarUsuarioPorId(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ? AND usuario != 'admin'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
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

    // Verificar credenciales por correo
    public boolean verificarCredencialesPorCorreo(String correo, String password) {
        String sql = "SELECT password FROM usuarios WHERE correo = ?";
        if (correo == null || password == null) {
            return false;
        }
        String c = correo.trim();
        String p = password.trim();
        if (c.isEmpty() || p.isEmpty()) {
            return false;
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, c);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String pwdDb = rs.getString("password");
                return pwdDb != null && pwdDb.equals(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Obtener información del usuario por id
    public static Usuario obtenerUsuarioPorId(int id) {
        String sql = "SELECT id, usuario, apellido, correo, password, rol, descripcion FROM usuarios WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Usuario(
                    rs.getInt("id"),
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

    public boolean crearUsuario(String usuario, String apellido, String correo, String password, String rol, String descripcion) {
        String sql = "INSERT INTO usuarios (usuario, apellido, correo, password, rol, descripcion) VALUES (?, ?, ?, ?, ?, ?)";
        
        if (usuario == null || password == null || rol == null) {
            return false;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usuario);
            pstmt.setString(2, apellido != null ? apellido : "");
            pstmt.setString(3, correo != null ? correo : "");
            pstmt.setString(4, password);
            pstmt.setString(5, rol);
            pstmt.setString(6, descripcion != null ? descripcion : "");
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obtener rol y descripcion por correo (compatibilidad con Autenticacion)
    public Map<String, String> obtenerInfoUsuarioPorCorreo(String correo) {
        Map<String, String> info = new HashMap<>();
        String sql = "SELECT rol, descripcion FROM usuarios WHERE correo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, correo);
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

    // Obtener usuario por correo (para autenticación)
    public static Usuario obtenerUsuarioPorCorreo(String correo) {
        String sql = "SELECT id, usuario, apellido, correo, password, rol, descripcion FROM usuarios WHERE correo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, correo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                        rs.getInt("id"),
                        rs.getString("usuario"),
                        rs.getString("apellido"),
                        rs.getString("correo"),
                        rs.getString("password"),
                        rs.getString("rol"),
                        rs.getString("descripcion")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}