package com.mycompany.controlfichaje.dao;

import jakarta.servlet.ServletContext;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static String URL;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("Driver SQLite cargado correctamente");
        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar el driver SQLite: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para inicializar la URL de la base de datos
    public static void init(ServletContext context) {
        String dbRelativePath = "/WEB-INF/fichajes.sqlite3";
        String realPath = context.getRealPath(dbRelativePath);
        if (realPath == null) {
            throw new IllegalStateException("No se pudo obtener la ruta real para: " + dbRelativePath);
        }
        URL = "jdbc:sqlite:" + realPath.replace("\\", "/");
        System.out.println("URL de la base de datos inicializada: " + URL);
    }

    public static Connection getConnection() throws SQLException {
        if (URL == null) {
            throw new IllegalStateException("La URL de la base de datos no está inicializada. Llama a init() primero.");
        }
        System.out.println("Conectando a la base de datos: " + URL);
        return DriverManager.getConnection(URL);
    }
}