package fr.usms.gestitaches;

import fr.usms.gestitaches.dao.ProjetDAO;
import fr.usms.gestitaches.dao.TacheDAO;
import fr.usms.gestitaches.service.ProjetService;
import fr.usms.gestitaches.service.TacheService;
import fr.usms.gestitaches.ui.MainFrame;

import javax.swing.SwingUtilities;

public class App {

    public static void main(String[] args) {
        ProjetDAO projetDAO = new ProjetDAO();
        TacheDAO tacheDAO = new TacheDAO();
        ProjetService projetService = new ProjetService(projetDAO);
        TacheService tacheService = new TacheService(tacheDAO, projetDAO);
        SwingUtilities.invokeLater(() ->
                new MainFrame(projetService, tacheService).setVisible(true));
    }
}
