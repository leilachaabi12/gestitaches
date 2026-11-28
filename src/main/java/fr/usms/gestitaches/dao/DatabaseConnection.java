package fr.usms.gestitaches.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Ouvre les connexions JDBC vers la base SQLite du projet.
 * La base est le fichier gestitaches.db, a la racine du projet.
 */
public final class DatabaseConnection {

    private static final String URL_SYSTEM_PROPERTY = "gestitaches.db.url";
    private static final String DEFAULT_URL = "jdbc:sqlite:gestitaches.db";

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        String url = System.getProperty(URL_SYSTEM_PROPERTY, DEFAULT_URL);
        Connection connexion = DriverManager.getConnection(url);
        // SQLite ne verifie les cles etrangeres que si on le lui demande, a chaque connexion.
        try (Statement instruction = connexion.createStatement()) {
            instruction.execute("PRAGMA foreign_keys = ON");
        }
        return connexion;
    }
}
