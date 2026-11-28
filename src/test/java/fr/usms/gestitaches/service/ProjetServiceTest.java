package fr.usms.gestitaches.service;

import fr.usms.gestitaches.dao.ProjetDAO;
import fr.usms.gestitaches.model.Projet;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProjetServiceTest {

    private static Path baseTemporaire;
    private ProjetDAO dao;
    private ProjetService service;

    @BeforeAll
    static void ouvrirBaseTemporaire() throws Exception {
        baseTemporaire = Files.createTempFile("gestitaches-us1-", ".db");
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
        dao = new ProjetDAO();
        service = new ProjetService(dao);
    }

    @Test
    void creerProjetAvecNomValide() {
        Projet projet = service.creerProjet("Site web");

        assertEquals("Site web", projet.getNom());
        assertNull(projet.getDescription());
        assertEquals(1, service.listerProjets().size());
    }

    @Test
    void creerProjetAvecDescriptionFacultative() {
        Projet projet = service.creerProjet("Site web", "  Refonte du site vitrine  ");

        assertEquals("Refonte du site vitrine", projet.getDescription());
        assertEquals("Refonte du site vitrine",
                service.consulterProjet(projet.getId()).getDescription());
    }

    @Test
    void convertirUneDescriptionVideEnNull() {
        Projet projet = service.creerProjet("Site web", "   ");

        assertNull(projet.getDescription());
    }

    @Test
    void creerProjetNormaliseLesEspacesExterieurs() {
        Projet projet = service.creerProjet("  Inventaire  ");

        assertEquals("Inventaire", projet.getNom());
    }

    @Test
    void refuserNomVide() {
        assertThrows(IllegalArgumentException.class,
                () -> service.creerProjet(""));
        assertEquals(0, service.listerProjets().size());
    }

    @Test
    void refuserNomComposeDespaces() {
        assertThrows(IllegalArgumentException.class,
                () -> service.creerProjet("   "));
        assertEquals(0, service.listerProjets().size());
    }

    @Test
    void refuserDoublonSansAjouterUneLigne() {
        service.creerProjet("Site web");

        assertThrows(IllegalArgumentException.class,
                () -> service.creerProjet("Site web"));
        assertEquals(1, service.listerProjets().size());
    }
}
