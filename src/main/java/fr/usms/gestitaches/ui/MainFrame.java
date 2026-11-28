package fr.usms.gestitaches.ui;

import fr.usms.gestitaches.dao.PersistanceException;
import fr.usms.gestitaches.model.Projet;
import fr.usms.gestitaches.model.Tache;
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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class MainFrame extends JFrame {

    private final ProjetService projetService;
    private final TacheService tacheService;
    private final DefaultListModel<Projet> projetsModel = new DefaultListModel<>();
    private final JList<Projet> listeProjets = new JList<>(projetsModel);
    private final DefaultListModel<Tache> tachesModel = new DefaultListModel<>();
    private final JList<Tache> listeTaches = new JList<>(tachesModel);
    private final JTextField champNom = new JTextField(20);
    private final JTextField champDescription = new JTextField(20);
    private final JTextField champTitreTache = new JTextField(20);
    private final JTextField champDescriptionTache = new JTextField(20);
    private final JTextField champEcheance = new JTextField(10);
    private final JLabel ficheProjet = new JLabel("Selectionnez un projet.");

    public MainFrame(ProjetService projetService, TacheService tacheService) {
        super("GestiTaches");
        this.projetService = projetService;
        this.tacheService = tacheService;

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
        listeTaches.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ficheProjet.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel panneauProjets = new JPanel(new BorderLayout());
        panneauProjets.add(new JLabel("Projets"), BorderLayout.NORTH);
        panneauProjets.add(new JScrollPane(listeProjets), BorderLayout.CENTER);

        JPanel formulaireTache = new JPanel(new GridLayout(4, 1));
        JPanel ligneTitre = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel ligneDescriptionTache = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel ligneEcheance = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton creerTache = new JButton("Ajouter la tâche");
        ligneTitre.add(new JLabel("Titre (obligatoire) :"));
        ligneTitre.add(champTitreTache);
        ligneDescriptionTache.add(new JLabel("Description (facultative) :"));
        ligneDescriptionTache.add(champDescriptionTache);
        ligneEcheance.add(new JLabel("Échéance (AAAA-MM-JJ, facultative) :"));
        ligneEcheance.add(champEcheance);
        formulaireTache.add(ligneTitre);
        formulaireTache.add(ligneDescriptionTache);
        formulaireTache.add(ligneEcheance);
        formulaireTache.add(creerTache);

        JPanel panneauTaches = new JPanel(new BorderLayout());
        panneauTaches.add(ficheProjet, BorderLayout.NORTH);
        panneauTaches.add(new JScrollPane(listeTaches), BorderLayout.CENTER);
        panneauTaches.add(formulaireTache, BorderLayout.SOUTH);

        JPanel contenu = new JPanel(new GridLayout(1, 2, 8, 0));
        contenu.add(panneauProjets);
        contenu.add(panneauTaches);

        add(formulaire, BorderLayout.NORTH);
        add(contenu, BorderLayout.CENTER);

        creer.addActionListener(event -> creerProjet());
        creerTache.addActionListener(event -> creerTache());
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

    private void creerTache() {
        Projet projet = listeProjets.getSelectedValue();
        if (projet == null || projet.getId() == null) {
            JOptionPane.showMessageDialog(this, "Selectionnez d'abord un projet.",
                    "Creation refusee", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDate echeance = champEcheance.getText().isBlank()
                    ? null
                    : LocalDate.parse(champEcheance.getText().trim());
            tacheService.creerTache(projet.getId(), champTitreTache.getText(),
                    champDescriptionTache.getText(), echeance);
            champTitreTache.setText("");
            champDescriptionTache.setText("");
            champEcheance.setText("");
            rafraichirTaches(projet.getId());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "L'échéance doit être une date valide au format AAAA-MM-JJ.",
                    "Creation refusee", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Creation refusee", JOptionPane.WARNING_MESSAGE);
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
            tachesModel.clear();
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
                rafraichirTaches(projet.getId());
            }
        } catch (PersistanceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rafraichirTaches(int projetId) {
        try {
            var taches = tacheService.listerParProjet(projetId);
            tachesModel.clear();
            for (Tache tache : taches) {
                tachesModel.addElement(tache);
            }
        } catch (IllegalArgumentException | PersistanceException e) {
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
