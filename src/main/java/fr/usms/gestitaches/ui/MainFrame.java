package fr.usms.gestitaches.ui;

import fr.usms.gestitaches.dao.PersistanceException;
import fr.usms.gestitaches.model.Projet;
import fr.usms.gestitaches.service.ProjetService;
import fr.usms.gestitaches.service.TacheService;

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
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

public class MainFrame extends JFrame {

    private final ProjetService projetService;
    private final TachePanel tachePanel;
    private final DefaultListModel<Projet> projetsModel = new DefaultListModel<>();
    private final JList<Projet> listeProjets = new JList<>(projetsModel);
    private final JTextField champNom = new JTextField(20);
    private final JTextField champDescription = new JTextField(20);
    private final JLabel ficheProjet = new JLabel("Selectionnez un projet.");

    public MainFrame(ProjetService projetService, TacheService tacheService) {
        super("GestiTaches");
        this.projetService = projetService;
        this.tachePanel = new TachePanel(tacheService);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 560);
        setLocationRelativeTo(null);
        construireInterface();
        rafraichirListe();
    }

    private void construireInterface() {
        JPanel formulaire = new JPanel(new GridLayout(2, 1));
        JPanel ligneNom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel ligneDescription = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton creer = new JButton("Creer");
        ligneNom.add(new JLabel("Nom du projet :"));
        ligneNom.add(champNom);
        ligneNom.add(creer);
        ligneDescription.add(new JLabel("Description (facultative) :"));
        ligneDescription.add(champDescription);
        formulaire.add(ligneNom);
        formulaire.add(ligneDescription);

        listeProjets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ficheProjet.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel panneauProjets = new JPanel(new BorderLayout());
        panneauProjets.add(new JLabel("Projets"), BorderLayout.NORTH);
        panneauProjets.add(new JScrollPane(listeProjets), BorderLayout.CENTER);

        JPanel panneauTaches = new JPanel(new BorderLayout());
        panneauTaches.add(ficheProjet, BorderLayout.NORTH);
        panneauTaches.add(tachePanel, BorderLayout.CENTER);

        JPanel contenu = new JPanel(new GridLayout(1, 2, 8, 0));
        contenu.add(panneauProjets);
        contenu.add(panneauTaches);

        add(formulaire, BorderLayout.NORTH);
        add(contenu, BorderLayout.CENTER);

        creer.addActionListener(event -> creerProjet());
        listeProjets.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                afficherProjetSelectionne();
            }
        });
    }

    private void creerProjet() {
        try {
            Projet projet = projetService.creerProjet(
                    champNom.getText(), champDescription.getText());
            champNom.setText("");
            champDescription.setText("");
            rafraichirListe();
            selectionnerProjet(projet.getId());
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
            ficheProjet.setText("Selectionnez un projet.");
            tachePanel.afficherProjet(null);
            return;
        }

        try {
            Projet projet = projetService.consulterProjet(projetSelectionne.getId());
            if (projet != null) {
                String description = projet.getDescription() == null
                        ? "Aucune description"
                        : projet.getDescription();
                ficheProjet.setText("Projet : " + projet.getNom() + " — Description : "
                        + description);
                tachePanel.afficherProjet(projet);
            }
        } catch (PersistanceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selectionnerProjet(Integer projetId) {
        if (projetId == null) {
            return;
        }
        for (int index = 0; index < projetsModel.size(); index++) {
            if (projetId.equals(projetsModel.get(index).getId())) {
                listeProjets.setSelectedIndex(index);
                return;
            }
        }
    }
}
