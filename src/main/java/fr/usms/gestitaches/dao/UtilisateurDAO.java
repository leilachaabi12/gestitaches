package fr.usms.gestitaches.dao;

import fr.usms.gestitaches.model.Utilisateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {

    public UtilisateurDAO() {
        initialiserTable();
    }

    private void initialiserTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS utilisateur (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE
                )
                """;

        try (Connection connexion = DatabaseConnection.getConnection();
             Statement instruction = connexion.createStatement()) {
            instruction.execute(sql);
        } catch (SQLException e) {
            throw new PersistanceException("Impossible d'initialiser la table utilisateur.", e);
        }
    }

    public List<Utilisateur> trouverTous() {
        String sql = "SELECT id, nom, email FROM utilisateur ORDER BY nom COLLATE NOCASE";
        List<Utilisateur> utilisateurs = new ArrayList<>();

        try (Connection connexion = DatabaseConnection.getConnection();
             PreparedStatement instruction = connexion.prepareStatement(sql);
             ResultSet resultat = instruction.executeQuery()) {
            while (resultat.next()) {
                utilisateurs.add(lireUtilisateur(resultat));
            }
            return utilisateurs;
        } catch (SQLException e) {
            throw new PersistanceException("Impossible de lire la liste des membres.", e);
        }
    }

    public Utilisateur trouverParId(int id) {
        String sql = "SELECT id, nom, email FROM utilisateur WHERE id = ?";

        try (Connection connexion = DatabaseConnection.getConnection();
             PreparedStatement instruction = connexion.prepareStatement(sql)) {
            instruction.setInt(1, id);
            try (ResultSet resultat = instruction.executeQuery()) {
                return resultat.next() ? lireUtilisateur(resultat) : null;
            }
        } catch (SQLException e) {
            throw new PersistanceException("Impossible de rechercher le membre.", e);
        }
    }

    private Utilisateur lireUtilisateur(ResultSet resultat) throws SQLException {
        return new Utilisateur(
                resultat.getInt("id"),
                resultat.getString("nom"),
                resultat.getString("email"));
    }
}
