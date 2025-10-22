package com.mycompany.controlfichaje.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Proveedor centralizado de conexiones JDBC a la base de datos SQLite.
 *
 * Detalles clave:
 * - Usa un fichero SQLite externo a la aplicación web para garantizar
 *   persistencia entre despliegues: C:/Users/Curso Tarde/fichajes.sqlite3
 * - Carga explícitamente el driver org.sqlite.JDBC en el bloque estático.
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:C:/Users/Curso Tarde/fichajes.sqlite3";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtiene una nueva conexión JDBC contra la URL configurada.
     * @return conexión abierta a SQLite
     * @throws SQLException si no es posible abrir la conexión
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}