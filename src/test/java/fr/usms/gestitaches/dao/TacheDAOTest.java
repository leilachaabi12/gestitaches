package fr.usms.gestitaches.dao;

import fr.usms.gestitaches.model.Tache;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TacheDAOTest {

    private static Path baseTemporaire;
    private ProjetDAO projetDAO;
    private TacheDAO tacheDAO;
    private int projetId;

    @BeforeAll
    static void ouvrirBaseTemporaire() throws Exception {
        baseTemporaire = Files.createTempFile("gestitaches-us2-dao-", ".db");
        Files.deleteIfExists(baseTemporaire);
        System.setProperty("gestitaches.db.url",
                "jdbc:sqlite:" + baseTemporaire.toAbsolutePath());
    }

    @AfterAll
    static void fermerBaseTemporaire() throws Exception {
        System.clearProperty("gestitaches.db.url");
        Files.deleteIfExists(baseTemporaire);
    }

    @BeforeEach
    void initialiserDaos() throws Exception {
        Files.deleteIfExists(baseTemporaire);
        projetDAO = new ProjetDAO();
        try (Connection connexion = DatabaseConnection.getConnection();
             Statement instruction = connexion.createStatement()) {
            instruction.execute("""
                    CREATE TABLE utilisateur (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nom TEXT NOT NULL,
                        email TEXT NOT NULL UNIQUE
                    )
                    """);
        }
        tacheDAO = new TacheDAO();
        projetId = projetDAO.creer("Projet test").getId();
    }

    @Test
    void creerEtRelireUneTacheAvecEcheanceNulle() {
        Tache creee = tacheDAO.creer(new Tache(null, "Maquette", null, "à faire",
                null, null, null, projetId));
        List<Tache> taches = tacheDAO.trouverParProjet(projetId);

        assertNotNull(creee.getId());
        assertEquals(1, taches.size());
        assertEquals("Maquette", taches.get(0).getTitre());
        assertEquals("à faire", taches.get(0).getStatut());
        assertNull(taches.get(0).getEcheance());
        assertNull(taches.get(0).getUtilisateurId());
        assertEquals(projetId, taches.get(0).getProjetId());
    }

    @Test
    void conserverDescriptionEtEcheance() {
        LocalDate echeance = LocalDate.of(2026, 12, 1);
        tacheDAO.creer(new Tache(null, "Maquette", "Créer une première version",
                "à faire", echeance, null, null, projetId));

        Tache tache = tacheDAO.trouverParProjet(projetId).getFirst();

        assertEquals("Créer une première version", tache.getDescription());
        assertEquals(echeance, tache.getEcheance());
    }

    @Test
    void ajouterUneTacheSansModifierLesTachesExistantes() {
        Tache premiere = tacheDAO.creer(new Tache(null, "Première tâche", null,
                "à faire", null, null, null, projetId));
        tacheDAO.creer(new Tache(null, "Nouvelle tâche", null,
                "à faire", null, null, null, projetId));

        List<Tache> taches = tacheDAO.trouverParProjet(projetId);

        assertEquals(2, taches.size());
        assertEquals(premiere.getId(), taches.getFirst().getId());
        assertEquals("Première tâche", taches.getFirst().getTitre());
    }

    @Test
    void changerLeStatutSansModifierLesAutresChamps() {
        Tache creee = tacheDAO.creer(new Tache(null, "Maquette", "Description",
                "à faire", null, null, null, projetId));

        boolean modifiee = tacheDAO.changerStatut(creee.getId(), "à faire", "en cours");
        Tache relue = tacheDAO.trouverParId(creee.getId());

        assertTrue(modifiee);
        assertEquals("en cours", relue.getStatut());
        assertEquals("Maquette", relue.getTitre());
        assertEquals("Description", relue.getDescription());
        assertEquals(projetId, relue.getProjetId());
    }

    @Test
    void refuserLaMiseAJourSiLeStatutCourantEstDevenuObsolete() {
        Tache creee = tacheDAO.creer(new Tache(null, "Maquette", null,
                "à faire", null, null, null, projetId));
        tacheDAO.changerStatut(creee.getId(), "à faire", "en cours");

        boolean modifiee = tacheDAO.changerStatut(creee.getId(), "à faire", "terminé");

        assertFalse(modifiee);
        assertEquals("en cours", tacheDAO.trouverParId(creee.getId()).getStatut());
    }
}
