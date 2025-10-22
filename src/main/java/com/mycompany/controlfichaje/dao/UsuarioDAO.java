package com.mycompany.controlfichaje.dao;

import java.sql.*;
import java.util.*;

/**
 * Acceso a datos (DAO) para la entidad Usuario.
 *
 * Expone operaciones CRUD y consultas auxiliares:
 * - Crear, actualizar y eliminar usuarios (por id).
 * - Listar usuarios para mostrarlos en JSP.
 * - Verificar credenciales (por correo) para la autenticación.
 * - Obtener datos de un usuario por id o por correo.
 */
public class UsuarioDAO {

    // Métodos de UsuarioDAO

    /**
     * Actualiza los datos de un usuario identificado por su id.
     * Si {@code password} está vacío, no se actualiza la contraseña.
     * @return true si se actualizó al menos una fila
     */
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

    /**
     * Devuelve una lista de mapas con los campos principales de todos los usuarios.
     * Se usa para renderizar tablas en JSP. Incluye el id como String.
     */
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

    /**
     * Elimina un usuario por id. Protege al usuario 'admin' para no borrarlo.
     */
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

    /**
     * Comprueba si ya existe un usuario con el nombre visible indicado.
     */
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

    /**
     * Verifica si la contraseña proporcionada coincide con la guardada para el correo dado.
     * (En este proyecto no hay hashing, se compara texto plano).
     */
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

    /**
     * Recupera un Usuario completo por id.
     */
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

    /**
     * Crea un nuevo usuario con los campos proporcionados.
     * Campos opcionales: apellido, correo y descripcion (se guardan como cadena vacía si son null).
     */
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

    /**
     * Obtiene rol y descripción de un usuario a partir de su correo.
     * Devuelve un Map con claves "rol" y "descripcion"; vacío si no existe.
     */
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

    /**
     * Recupera un Usuario completo por correo. Usado durante la autenticación.
     */
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