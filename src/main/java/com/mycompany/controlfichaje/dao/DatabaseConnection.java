package com.mycompany.controlfichaje.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:C:/Users/Usuario/App_fichaje-jose/fichajes.sqlite3";

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