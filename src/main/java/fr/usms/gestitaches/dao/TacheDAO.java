package fr.usms.gestitaches.dao;

import fr.usms.gestitaches.model.Tache;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TacheDAO {

    public TacheDAO() {
        initialiserTable();
    }

    private void initialiserTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS tache (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    titre TEXT NOT NULL,
                    description TEXT,
                    statut TEXT NOT NULL DEFAULT 'à faire'
                        CHECK (statut IN ('à faire', 'en cours', 'terminé')),
                    echeance DATE,
                    date_limite DATE,
                    utilisateur_id INTEGER REFERENCES utilisateur(id),
                    projet_id INTEGER NOT NULL REFERENCES projet(id)
                )
                """;

        try (Connection connexion = DatabaseConnection.getConnection();
             Statement instruction = connexion.createStatement()) {
            instruction.execute(sql);
        } catch (SQLException e) {
            throw new PersistanceException("Impossible d'initialiser la table tache.", e);
        }
    }

    public Tache creer(Tache tache) {
        String sql = """
                INSERT INTO tache
                    (titre, description, statut, echeance, date_limite, utilisateur_id, projet_id)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connexion = DatabaseConnection.getConnection();
             PreparedStatement instruction =
                     connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            instruction.setString(1, tache.getTitre());
            instruction.setString(2, tache.getDescription());
            instruction.setString(3, tache.getStatut());
            setDate(instruction, 4, tache.getEcheance());
            setDate(instruction, 5, tache.getDateLimite());
            if (tache.getUtilisateurId() == null) {
                instruction.setNull(6, Types.INTEGER);
            } else {
                instruction.setInt(6, tache.getUtilisateurId());
            }
            instruction.setInt(7, tache.getProjetId());
            instruction.executeUpdate();

            try (ResultSet clefs = instruction.getGeneratedKeys()) {
                if (clefs.next()) {
                    return new Tache(clefs.getInt(1), tache.getTitre(), tache.getDescription(),
                            tache.getStatut(), tache.getEcheance(), tache.getDateLimite(),
                            tache.getUtilisateurId(), tache.getProjetId());
                }
            }
            throw new PersistanceException("La tâche a été créée sans identifiant.");
        } catch (SQLException e) {
            throw new PersistanceException("Impossible de créer la tâche.", e);
        }
    }

    public List<Tache> trouverParProjet(int projetId) {
        String sql = """
                SELECT id, titre, description, statut, echeance, date_limite,
                       utilisateur_id, projet_id
                FROM tache
                WHERE projet_id = ?
                ORDER BY id
                """;
        List<Tache> taches = new ArrayList<>();

        try (Connection connexion = DatabaseConnection.getConnection();
             PreparedStatement instruction = connexion.prepareStatement(sql)) {
            instruction.setInt(1, projetId);
            try (ResultSet resultat = instruction.executeQuery()) {
                while (resultat.next()) {
                    taches.add(lireTache(resultat));
                }
            }
            return taches;
        } catch (SQLException e) {
            throw new PersistanceException("Impossible de lire les tâches du projet.", e);
        }
    }

    public Tache trouverParId(int id) {
        String sql = """
                SELECT id, titre, description, statut, echeance, date_limite,
                       utilisateur_id, projet_id
                FROM tache
                WHERE id = ?
                """;

        try (Connection connexion = DatabaseConnection.getConnection();
             PreparedStatement instruction = connexion.prepareStatement(sql)) {
            instruction.setInt(1, id);
            try (ResultSet resultat = instruction.executeQuery()) {
                return resultat.next() ? lireTache(resultat) : null;
            }
        } catch (SQLException e) {
            throw new PersistanceException("Impossible de rechercher la tâche.", e);
        }
    }

    public void assignerUtilisateur(int tacheId, Integer utilisateurId) {
        String sql = "UPDATE tache SET utilisateur_id = ? WHERE id = ?";

        try (Connection connexion = DatabaseConnection.getConnection();
             PreparedStatement instruction = connexion.prepareStatement(sql)) {
            if (utilisateurId == null) {
                instruction.setNull(1, Types.INTEGER);
            } else {
                instruction.setInt(1, utilisateurId);
            }
            instruction.setInt(2, tacheId);
            if (instruction.executeUpdate() == 0) {
                throw new IllegalArgumentException("La tâche sélectionnée n'existe pas.");
            }
        } catch (SQLException e) {
            throw new PersistanceException("Impossible d'assigner le membre à la tâche.", e);
        }
    }

    private Tache lireTache(ResultSet resultat) throws SQLException {
        int utilisateurIdLu = resultat.getInt("utilisateur_id");
        Integer utilisateurId = resultat.wasNull() ? null : utilisateurIdLu;
        return new Tache(
                resultat.getInt("id"),
                resultat.getString("titre"),
                resultat.getString("description"),
                resultat.getString("statut"),
                lireDate(resultat, "echeance"),
                lireDate(resultat, "date_limite"),
                utilisateurId,
                resultat.getInt("projet_id"));
    }

    private void setDate(PreparedStatement instruction, int index, LocalDate date)
            throws SQLException {
        if (date == null) {
            instruction.setNull(index, Types.DATE);
        } else {
            instruction.setString(index, date.toString());
        }
    }

    private LocalDate lireDate(ResultSet resultat, String colonne) throws SQLException {
        String date = resultat.getString(colonne);
        return date == null ? null : LocalDate.parse(date);
    }
}
