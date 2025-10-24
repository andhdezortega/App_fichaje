package com.mycompany.controlfichaje.dao;

import com.mycompany.controlfichaje.FichajeModel;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class FichajeDAO {

    
    public boolean crear(FichajeModel fichaje) {
        String sql = "INSERT INTO fichajes (nombre, apellido, rol, fecha, entrada, salida, descanso, comida, horas_semanales, estado) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Obtener horas_semanales desde la tabla usuarios
            int horasSemanales = 40; // Valor por defecto
            String sqlUsuario = "SELECT horas_semanales FROM usuarios WHERE usuario = ? LIMIT 1";
            try (PreparedStatement pstmtUsuario = conn.prepareStatement(sqlUsuario)) {
                pstmtUsuario.setString(1, fichaje.getNombre());
                ResultSet rsUsuario = pstmtUsuario.executeQuery();
                if (rsUsuario.next()) {
                    horasSemanales = rsUsuario.getInt("horas_semanales");
                }
            }
            
            pstmt.setString(1, fichaje.getNombre());
            pstmt.setString(2, fichaje.getApellido());
            pstmt.setString(3, fichaje.getRol());
            pstmt.setString(4, fichaje.getFecha() != null ? fichaje.getFecha().toString() : "");
            pstmt.setString(5, fichaje.getEntrada() != null ? fichaje.getEntrada().toString() : "");
            pstmt.setString(6, fichaje.getSalida() != null ? fichaje.getSalida().toString() : "");
            pstmt.setInt(7, fichaje.getDescanso());
            pstmt.setInt(8, fichaje.getComida());
            pstmt.setInt(9, horasSemanales);
            pstmt.setBoolean(10, fichaje.isEstado());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Recupera todos los fichajes existentes ordenados por fecha y hora de entrada descendente (más recientes primero).
     */
    public List<FichajeModel> obtenerTodos() {
        List<FichajeModel> fichajes = new ArrayList<>();
        String sql = "SELECT * FROM fichajes ORDER BY fecha DESC, entrada DESC, id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String fechaStr = rs.getString("fecha");
                String entradaStr = rs.getString("entrada");
                String salidaStr = rs.getString("salida");
                LocalDate fecha = (fechaStr != null && !fechaStr.isEmpty()) ? LocalDate.parse(fechaStr) : null;
                LocalTime entrada = (entradaStr != null && !entradaStr.isEmpty()) ? LocalTime.parse(entradaStr) : null;
                LocalTime salida = (salidaStr != null && !salidaStr.isEmpty()) ? LocalTime.parse(salidaStr) : null;

                FichajeModel fichaje = new FichajeModel(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("rol"),
                    fecha,
                    entrada,
                    salida,
                    rs.getInt("descanso"),
                    rs.getInt("comida"),
                    rs.getInt("horas_semanales"),
                    rs.getBoolean("estado")
                );
                try { fichaje.setHorasExtra(rs.getInt("horas_extra")); } catch (SQLException ignore) {}
                fichajes.add(fichaje);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fichajes;
    }

    

    /**
     * Recupera un fichaje específico por su ID.
     */
    public FichajeModel obtenerPorId(int id) {
        String sql = "SELECT * FROM fichajes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String fechaStr = rs.getString("fecha");
                    String entradaStr = rs.getString("entrada");
                    String salidaStr = rs.getString("salida");
                    java.time.LocalDate fecha = (fechaStr != null && !fechaStr.isEmpty()) ? java.time.LocalDate.parse(fechaStr) : null;
                    java.time.LocalTime entrada = (entradaStr != null && !entradaStr.isEmpty()) ? java.time.LocalTime.parse(entradaStr) : null;
                    java.time.LocalTime salida = (salidaStr != null && !salidaStr.isEmpty()) ? java.time.LocalTime.parse(salidaStr) : null;

                    FichajeModel f = new FichajeModel(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("rol"),
                        fecha,
                        entrada,
                        salida,
                        rs.getInt("descanso"),
                        rs.getInt("comida"),
                        rs.getInt("horas_semanales"),
                        rs.getBoolean("estado")
                    );
                    try { f.setHorasExtra(rs.getInt("horas_extra")); } catch (SQLException ignore) {}
                    return f;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Recupera el último fichaje activo (estado = 0) para un usuario (nombre/apellido).
     */
    public FichajeModel obtenerFichajeActivo(String nombre, String apellido) {
        String sql = "SELECT * FROM fichajes WHERE nombre = ? AND apellido = ? AND estado = 0 ORDER BY id DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, apellido);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String fechaStr = rs.getString("fecha");
                    String entradaStr = rs.getString("entrada");
                    String salidaStr = rs.getString("salida");
                    java.time.LocalDate fecha = (fechaStr != null && !fechaStr.isEmpty()) ? java.time.LocalDate.parse(fechaStr) : null;
                    java.time.LocalTime entrada = (entradaStr != null && !entradaStr.isEmpty()) ? java.time.LocalTime.parse(entradaStr) : null;
                    java.time.LocalTime salida = (salidaStr != null && !salidaStr.isEmpty()) ? java.time.LocalTime.parse(salidaStr) : null;

                    FichajeModel f = new FichajeModel(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("rol"),
                        fecha,
                        entrada,
                        salida,
                        rs.getInt("descanso"),
                        rs.getInt("comida"),
                        rs.getInt("horas_semanales"),
                        rs.getBoolean("estado")
                    );
                    try { f.setHorasExtra(rs.getInt("horas_extra")); } catch (SQLException ignore) {}
                    return f;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Actualiza un fichaje existente por id.
     */
    public boolean actualizar(FichajeModel fichaje) {
        // Calcular horas extra automáticamente si hay entrada y salida
        int horasExtra = 0;
        if (fichaje.getEntrada() != null && fichaje.getSalida() != null) {
            long minutosTrabajados = java.time.Duration.between(fichaje.getEntrada(), fichaje.getSalida()).toMinutes();
            int descuentos = Math.max(0, fichaje.getDescanso()) + Math.max(0, fichaje.getComida());
            long minutosEfectivos = Math.max(0, minutosTrabajados - descuentos);
            long minutosBase = 8 * 60; // 8 horas
            horasExtra = (int) Math.max(0, minutosEfectivos - minutosBase);
        }
        
        String sql = "UPDATE fichajes SET nombre = ?, apellido = ?, rol = ?, fecha = ?, entrada = ?, " +
                    "salida = ?, descanso = ?, comida = ?, horas_semanales = ?, estado = ?, horas_extra = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, fichaje.getNombre());
            pstmt.setString(2, fichaje.getApellido());
            pstmt.setString(3, fichaje.getRol());
            pstmt.setString(4, fichaje.getFecha() != null ? fichaje.getFecha().toString() : "");
            pstmt.setString(5, fichaje.getEntrada() != null ? fichaje.getEntrada().toString() : "");
            pstmt.setString(6, fichaje.getSalida() != null ? fichaje.getSalida().toString() : "");
            pstmt.setInt(7, fichaje.getDescanso());
            pstmt.setInt(8, fichaje.getComida());
            pstmt.setInt(9, fichaje.getHorasSemanales());
            pstmt.setBoolean(10, fichaje.isEstado());
            pstmt.setInt(11, horasExtra);
            pstmt.setInt(12, fichaje.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina un fichaje por id.
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM fichajes WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}