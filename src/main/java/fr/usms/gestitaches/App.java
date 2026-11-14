package fr.usms.gestitaches;

import fr.usms.gestitaches.dao.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Point d'entree du depot de depart : verifie seulement que le projet
 * compile et que la connexion a la base SQLite fonctionne.
 * L'application elle-meme sera construite a partir de la Seance 2.
 */
public class App {

    public static void main(String[] args) {
        System.out.println("GestiTaches - depot de depart");
        try (Connection connexion = DatabaseConnection.getConnection()) {
            System.out.println("Connexion a la base gestitaches.db : OK");
        } catch (SQLException e) {
            System.out.println("Connexion a la base gestitaches.db : ECHEC");
            System.out.println(e.getMessage());
        }
    }
}
