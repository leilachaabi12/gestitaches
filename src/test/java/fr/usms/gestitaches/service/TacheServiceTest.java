package fr.usms.gestitaches.service;

import fr.usms.gestitaches.dao.ProjetDAO;
import fr.usms.gestitaches.dao.TacheDAO;
import fr.usms.gestitaches.dao.DatabaseConnection;
import fr.usms.gestitaches.model.Projet;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TacheServiceTest {

    private static Path baseTemporaire;
    private ProjetDAO projetDAO;
    private TacheDAO tacheDAO;
    private TacheService service;
    private int projetId;

    @BeforeAll
    static void ouvrirBaseTemporaire() throws Exception {
        baseTemporaire = Files.createTempFile("gestitaches-us2-service-", ".db");
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
    void initialiserService() throws Exception {
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
        service = new TacheService(tacheDAO, projetDAO);
        Projet projet = projetDAO.creer("Projet test");
        projetId = projet.getId();
    }

    @Test
    void creerTacheDansUnProjetAvecStatutInitialEtSansMembre() {
        Tache tache = service.creerTache(projetId, "Maquette de la page d'accueil",
                null, null);

        assertEquals("Maquette de la page d'accueil", tache.getTitre());
        assertEquals("à faire", tache.getStatut());
        assertNull(tache.getUtilisateurId());
        assertNull(tache.getEcheance());
        assertEquals(projetId, tache.getProjetId());
    }

    @Test
    void creerTacheAvecDescriptionEtEcheance() {
        LocalDate echeance = LocalDate.of(2026, 12, 1);

        Tache tache = service.creerTache(projetId, "Maquette",
                "Première version", echeance);

        assertEquals("Première version", tache.getDescription());
        assertEquals(echeance, tache.getEcheance());
    }

    @Test
    void refuserTitreVideSansModifierLesTachesExistantes() {
        service.creerTache(projetId, "Tâche existante", null, null);

        assertThrows(IllegalArgumentException.class,
                () -> service.creerTache(projetId, "   ", null, null));
        assertEquals(List.of("Tâche existante"), titresDesTaches());
    }

    @Test
    void refuserTitreNullSansModifierLesTachesExistantes() {
        service.creerTache(projetId, "Tâche existante", null, null);

        assertThrows(IllegalArgumentException.class,
                () -> service.creerTache(projetId, null, null, null));
        assertEquals(List.of("Tâche existante"), titresDesTaches());
    }

    @Test
    void refuserUnStatutInitialDifferentDeAFaire() {
        assertThrows(IllegalArgumentException.class,
                () -> service.creerTache(projetId, "Tâche", null, null, "en cours"));
        assertEquals(0, service.listerParProjet(projetId).size());
    }

    @Test
    void refuserUnProjetInexistant() {
        assertThrows(IllegalArgumentException.class,
                () -> service.creerTache(Integer.MAX_VALUE, "Tâche", null, null));
        assertEquals(0, service.listerParProjet(projetId).size());
    }

    private List<String> titresDesTaches() {
        return service.listerParProjet(projetId).stream()
                .map(Tache::getTitre)
                .toList();
    }
}
