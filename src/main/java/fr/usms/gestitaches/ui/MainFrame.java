package fr.usms.gestitaches.ui;

import fr.usms.gestitaches.dao.PersistanceException;
import fr.usms.gestitaches.dao.ProjetDAO;
import fr.usms.gestitaches.model.Projet;
import fr.usms.gestitaches.service.ProjetService;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class MainFrame extends JFrame {

    private final ProjetService projetService;
    private final DefaultListModel<Projet> projetsModel = new DefaultListModel<>();
    private final JList<Projet> listeProjets = new JList<>(projetsModel);
    private final JTextField champNom = new JTextField(20);
    private final JLabel ficheProjet = new JLabel("Selectionnez un projet.");

    public MainFrame() {
        super("GestiTaches");
        projetService = new ProjetService(new ProjetDAO());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 360);
        setLocationRelativeTo(null);
        construireInterface();
        rafraichirListe();
    }

    private void construireInterface() {
        JPanel formulaire = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton creer = new JButton("Creer");
        formulaire.add(new JLabel("Nom du projet :"));
        formulaire.add(champNom);
        formulaire.add(creer);

        listeProjets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listeDefilante = new JScrollPane(listeProjets);

        ficheProjet.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(formulaire, BorderLayout.NORTH);
        add(listeDefilante, BorderLayout.CENTER);
        add(ficheProjet, BorderLayout.SOUTH);

        creer.addActionListener(event -> creerProjet());
        listeProjets.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                afficherProjetSelectionne();
            }
        });
    }

    private void creerProjet() {
        try {
            projetService.creerProjet(champNom.getText());
            champNom.setText("");
            rafraichirListe();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Creation refusee",
                    JOptionPane.WARNING_MESSAGE);
        } catch (PersistanceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rafraichirListe() {
        projetsModel.clear();
        try {
            for (Projet projet : projetService.listerProjets()) {
                projetsModel.addElement(projet);
            }
        } catch (PersistanceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void afficherProjetSelectionne() {
        Projet projetSelectionne = listeProjets.getSelectedValue();
        if (projetSelectionne == null || projetSelectionne.getId() == null) {
            return;
        }

        try {
            Projet projet = projetService.consulterProjet(projetSelectionne.getId());
            if (projet != null) {
                ficheProjet.setText("Projet : " + projet.getNom());
            }
        } catch (PersistanceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
