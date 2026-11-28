package fr.usms.gestitaches.dao;

import fr.usms.gestitaches.model.Projet;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjetDAOTest {

    private static Path baseTemporaire;
    private ProjetDAO dao;

    @BeforeAll
    static void ouvrirBaseTemporaire() throws Exception {
        baseTemporaire = Files.createTempFile("gestitaches-us1-dao-", ".db");
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
    void initialiserDao() throws Exception {
        Files.deleteIfExists(baseTemporaire);
        dao = new ProjetDAO();
    }

    @Test
    void creerPuisLireUnProjet() {
        Projet cree = dao.creer("Site web");
        Projet lu = dao.trouverParId(cree.getId());

        assertNotNull(cree.getId());
        assertEquals("Site web", lu.getNom());
    }

    @Test
    void listerTousLesProjets() {
        dao.creer("Site web");
        dao.creer("Inventaire");

        List<Projet> projets = dao.trouverTous();

        assertEquals(2, projets.size());
    }

    @Test
    void detecterUnNomExistant() {
        dao.creer("Site web");

        assertTrue(dao.existeParNom("Site web"));
    }

    @Test
    void retournerUneListeVideAuDemarrage() {
        assertTrue(dao.trouverTous().isEmpty());
    }
}
