package fr.usms.gestitaches.service;

import fr.usms.gestitaches.dao.ProjetDAO;
import fr.usms.gestitaches.dao.TacheDAO;
import fr.usms.gestitaches.dao.DatabaseConnection;
import fr.usms.gestitaches.dao.UtilisateurDAO;
import fr.usms.gestitaches.model.Projet;
import fr.usms.gestitaches.model.Tache;
import fr.usms.gestitaches.model.Utilisateur;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    private UtilisateurDAO utilisateurDAO;
    private TacheService service;
    private int projetId;
    private int premierUtilisateurId;
    private int secondUtilisateurId;

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
        utilisateurDAO = new UtilisateurDAO();
        service = new TacheService(tacheDAO, projetDAO, utilisateurDAO);
        Projet projet = projetDAO.creer("Projet test");
        projetId = projet.getId();
        premierUtilisateurId = creerUtilisateur("Alice");
        secondUtilisateurId = creerUtilisateur("Bob");
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

    @Test
    void listerLesMembresDisponiblesPourLInterface() {
        List<Utilisateur> utilisateurs = service.listerUtilisateurs();

        assertEquals(List.of("Alice", "Bob"),
                utilisateurs.stream().map(Utilisateur::getNom).toList());
    }

    @Test
    void assignerUneTacheSansResponsableAUnMembreExistant() {
        Tache tache = service.creerTache(projetId, "Tâche à assigner", null, null);

        Tache assignee = service.assignerUtilisateur(tache.getId(), premierUtilisateurId);

        assertEquals(premierUtilisateurId, assignee.getUtilisateurId());
        assertEquals(premierUtilisateurId,
                service.listerParProjet(projetId).getFirst().getUtilisateurId());
    }

    @Test
    void remplacerLeResponsablePrecedentParLeSecondMembre() {
        Tache tache = service.creerTache(projetId, "Tâche à réassigner", null, null);
        service.assignerUtilisateur(tache.getId(), premierUtilisateurId);

        Tache reassignee = service.assignerUtilisateur(tache.getId(), secondUtilisateurId);

        assertEquals(secondUtilisateurId, reassignee.getUtilisateurId());
        assertEquals(1, service.listerParProjet(projetId).size());
    }

    @Test
    void refuserUnMembreInexistantSansChangerLeResponsableActuel() {
        Tache tache = service.creerTache(projetId, "Tâche protégée", null, null);
        service.assignerUtilisateur(tache.getId(), premierUtilisateurId);

        assertThrows(IllegalArgumentException.class,
                () -> service.assignerUtilisateur(tache.getId(), Integer.MAX_VALUE));

        assertEquals(premierUtilisateurId,
                service.listerParProjet(projetId).getFirst().getUtilisateurId());
    }

    @Test
    void retirerLeResponsablePourLaisserLaTacheNonAssignee() {
        Tache tache = service.creerTache(projetId, "Tâche sans responsable", null, null);
        service.assignerUtilisateur(tache.getId(), premierUtilisateurId);

        Tache nonAssignee = service.assignerUtilisateur(tache.getId(), null);

        assertNull(nonAssignee.getUtilisateurId());
        assertNull(service.listerParProjet(projetId).getFirst().getUtilisateurId());
    }

    @Test
    void passerDeAFaireAEnCours() {
        Tache tache = service.creerTache(projetId, "Tâche", null, null);

        Tache modifiee = service.changerStatut(tache.getId(), "en cours");

        assertEquals("en cours", modifiee.getStatut());
    }

    @Test
    void passerDeAFaireATermine() {
        Tache tache = service.creerTache(projetId, "Tâche", null, null);

        Tache modifiee = service.changerStatut(tache.getId(), "terminé");

        assertEquals("terminé", modifiee.getStatut());
    }

    @Test
    void passerDeEnCoursATermine() {
        Tache tache = service.creerTache(projetId, "Tâche", null, null);
        service.changerStatut(tache.getId(), "en cours");

        Tache modifiee = service.changerStatut(tache.getId(), "terminé");

        assertEquals("terminé", modifiee.getStatut());
    }

    @Test
    void refuserRetourDeEnCoursAVFaireSansChangerLeStatut() {
        Tache tache = service.creerTache(projetId, "Tâche", null, null);
        service.changerStatut(tache.getId(), "en cours");

        assertThrows(IllegalArgumentException.class,
                () -> service.changerStatut(tache.getId(), "à faire"));

        assertEquals("en cours", tacheDAO.trouverParId(tache.getId()).getStatut());
    }

    @Test
    void refuserUnStatutNonAutoriseSansChangerLeStatut() {
        Tache tache = service.creerTache(projetId, "Tâche", null, null);

        assertThrows(IllegalArgumentException.class,
                () -> service.changerStatut(tache.getId(), "bloquée"));

        assertEquals("à faire", tacheDAO.trouverParId(tache.getId()).getStatut());
    }

    @Test
    void figerLeStatutUneFoisTermine() {
        Tache tache = service.creerTache(projetId, "Tâche", null, null);
        service.changerStatut(tache.getId(), "terminé");

        assertThrows(IllegalArgumentException.class,
                () -> service.changerStatut(tache.getId(), "en cours"));

        assertEquals("terminé", tacheDAO.trouverParId(tache.getId()).getStatut());
    }

    @Test
    void refuserLeChangementDuStatutDUneTacheInexistante() {
        assertThrows(IllegalArgumentException.class,
                () -> service.changerStatut(Integer.MAX_VALUE, "en cours"));
    }

    private int creerUtilisateur(String nom) throws SQLException {
        String sql = "INSERT INTO utilisateur (nom, email) VALUES (?, ?)";
        try (Connection connexion = DatabaseConnection.getConnection();
             PreparedStatement instruction =
                     connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            instruction.setString(1, nom);
            instruction.setString(2, nom.toLowerCase() + "@example.test");
            instruction.executeUpdate();
            try (ResultSet clefs = instruction.getGeneratedKeys()) {
                if (!clefs.next()) {
                    throw new SQLException("Aucun identifiant de test généré.");
                }
                return clefs.getInt(1);
            }
        }
    }

    private List<String> titresDesTaches() {
        return service.listerParProjet(projetId).stream()
                .map(Tache::getTitre)
                .toList();
    }
}
