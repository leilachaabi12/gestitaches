package fr.usms.gestitaches.dao;

import fr.usms.gestitaches.model.Projet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProjetDAO {

    public ProjetDAO() {
        initialiserTable();
    }

    private void initialiserTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS projet (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL UNIQUE,
                    description TEXT
                )
                """;

        try (Connection connexion = DatabaseConnection.getConnection();
             Statement instruction = connexion.createStatement()) {
            instruction.execute(sql);
        } catch (SQLException e) {
            throw new PersistanceException("Impossible d'initialiser la table projet.", e);
        }
    }

    public boolean existeParNom(String nom) {
        String sql = "SELECT 1 FROM projet WHERE nom = ? LIMIT 1";

        try (Connection connexion = DatabaseConnection.getConnection();
             PreparedStatement instruction = connexion.prepareStatement(sql)) {
            instruction.setString(1, nom);
            try (ResultSet resultat = instruction.executeQuery()) {
                return resultat.next();
            }
        } catch (SQLException e) {
            throw new PersistanceException("Impossible de rechercher le projet.", e);
        }
    }

    public Projet creer(String nom) {
        String sql = "INSERT INTO projet (nom, description) VALUES (?, NULL)";

        try (Connection connexion = DatabaseConnection.getConnection();
             PreparedStatement instruction =
                     connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            instruction.setString(1, nom);
            instruction.executeUpdate();

            try (ResultSet clefs = instruction.getGeneratedKeys()) {
                if (clefs.next()) {
                    return new Projet(clefs.getInt(1), nom, null);
                }
            }
            throw new PersistanceException("Le projet a ete cree sans identifiant.");
        } catch (SQLException e) {
            throw new PersistanceException("Impossible de creer le projet.", e);
        }
    }

    public List<Projet> trouverTous() {
        String sql = "SELECT id, nom, description FROM projet ORDER BY id";
        List<Projet> projets = new ArrayList<>();

        try (Connection connexion = DatabaseConnection.getConnection();
             PreparedStatement instruction = connexion.prepareStatement(sql);
             ResultSet resultat = instruction.executeQuery()) {
            while (resultat.next()) {
                projets.add(new Projet(
                        resultat.getInt("id"),
                        resultat.getString("nom"),
                        resultat.getString("description")));
            }
            return projets;
        } catch (SQLException e) {
            throw new PersistanceException("Impossible de lire les projets.", e);
        }
    }

    public Projet trouverParId(int id) {
        String sql = "SELECT id, nom, description FROM projet WHERE id = ?";

        try (Connection connexion = DatabaseConnection.getConnection();
             PreparedStatement instruction = connexion.prepareStatement(sql)) {
            instruction.setInt(1, id);
            try (ResultSet resultat = instruction.executeQuery()) {
                if (resultat.next()) {
                    return new Projet(
                            resultat.getInt("id"),
                            resultat.getString("nom"),
                            resultat.getString("description"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new PersistanceException("Impossible de lire la fiche du projet.", e);
        }
    }
}
