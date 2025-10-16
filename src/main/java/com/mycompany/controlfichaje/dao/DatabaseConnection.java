package com.mycompany.controlfichaje.dao;

import java.sql.*;

public class DatabaseConnection {
    private static final String DB_NAME = "fichajes.sqlite3";
    private static final String PROJECT_PATH = "C:/Users/Curso Tarde/Desktop/Repositorio/App_fichaje/ControlFichaje";
    private static final String URL = "jdbc:sqlite:" + PROJECT_PATH + "/" + DB_NAME;
    
    static {
        // Imprimir la ruta donde se creará la base de datos
        System.out.println("Base de datos se creará en: " + PROJECT_PATH + "/" + DB_NAME);
        try {
            System.out.println("Intentando cargar el driver SQLite...");
            Class.forName("org.sqlite.JDBC");
            System.out.println("Driver SQLite cargado correctamente");
            System.out.println("URL de la base de datos: " + URL);
            initDatabase();
            System.out.println("Base de datos inicializada correctamente");
        } catch (Exception e) {
            System.err.println("Error crítico al inicializar la base de datos: " + e.getMessage());
            System.err.println("URL que causó el error: " + URL);
            e.printStackTrace();
        }
    }
    
    private static void initDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Crear tabla usuarios si no existe
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS usuarios (" +
                "usuario TEXT PRIMARY KEY," +
                "apellido TEXT," +
                "correo TEXT," +
                "password TEXT NOT NULL," +
                "rol TEXT NOT NULL," +
                "descripcion TEXT" +
                ")"
            );
            
            // Crear tabla fichajes si no existe
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS fichajes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "apellido TEXT NOT NULL," +
                "rol TEXT NOT NULL," +
                "fecha TEXT NOT NULL," +
                "entrada TEXT NOT NULL," +
                "salida TEXT," +
                "descanso INTEGER," +
                "comida INTEGER," +
                "horas_semanales INTEGER," +
                "estado BOOLEAN DEFAULT 0" +
                ")"
            );
            
            // Insertar usuario admin por defecto si no existe
            stmt.execute(
                "INSERT OR IGNORE INTO usuarios (usuario, apellido, correo, password, rol, descripcion) " +
                "VALUES ('admin', 'Sistema', 'admin@correo.com', 'admin', 'admin', 'Administrador del sistema')");
            
        } catch (SQLException e) {
            System.err.println("Error SQL al inicializar la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
            throw e;
        }
    }
}