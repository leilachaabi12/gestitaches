package fr.usms.gestitaches.ui;

import fr.usms.gestitaches.dao.PersistanceException;
import fr.usms.gestitaches.model.Projet;
import fr.usms.gestitaches.model.Tache;
import fr.usms.gestitaches.model.Utilisateur;
import fr.usms.gestitaches.service.TacheService;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TachePanel extends JPanel {

    private final TacheService tacheService;
    private final DefaultListModel<Tache> tachesModel = new DefaultListModel<>();
    private final JList<Tache> listeTaches = new JList<>(tachesModel);
    private final DefaultComboBoxModel<Utilisateur> utilisateursModel =
            new DefaultComboBoxModel<>();
    private final JComboBox<Utilisateur> listeUtilisateurs =
            new JComboBox<>(utilisateursModel);
    private final DefaultComboBoxModel<String> statutsModel = new DefaultComboBoxModel<>();
    private final JComboBox<String> listeStatuts = new JComboBox<>(statutsModel);
    private final JButton changerStatut = new JButton("Changer le statut");
    private final JTextField champTitre = new JTextField(20);
    private final JTextField champDescription = new JTextField(20);
    private final JTextField champEcheance = new JTextField(10);
    private final JLabel titreProjet = new JLabel("Selectionnez un projet.");
    private final Map<Integer, String> nomsUtilisateurs = new HashMap<>();
    private Projet projet;

    public TachePanel(TacheService tacheService) {
        super(new BorderLayout(4, 4));
        this.tacheService = tacheService;
        construireInterface();
        chargerUtilisateurs();
    }

    private void construireInterface() {
        titreProjet.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        listeTaches.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listeTaches.setCellRenderer(creerRenduTache());
        listeUtilisateurs.setRenderer(creerRenduUtilisateur());

        JPanel formulaireCreation = new JPanel(new GridLayout(4, 1));
        JPanel ligneTitre = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel ligneDescription = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel ligneEcheance = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton creer = new JButton("Ajouter la tâche");
        ligneTitre.add(new JLabel("Titre (obligatoire) :"));
        ligneTitre.add(champTitre);
        ligneDescription.add(new JLabel("Description (facultative) :"));
        ligneDescription.add(champDescription);
        ligneEcheance.add(new JLabel("Échéance (AAAA-MM-JJ, facultative) :"));
        ligneEcheance.add(champEcheance);
        formulaireCreation.add(ligneTitre);
        formulaireCreation.add(ligneDescription);
        formulaireCreation.add(ligneEcheance);
        formulaireCreation.add(creer);

        JPanel affectation = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton assigner = new JButton("Enregistrer le responsable");
        affectation.add(new JLabel("Responsable :"));
        affectation.add(listeUtilisateurs);
        affectation.add(assigner);

        JPanel ligneStatut = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ligneStatut.add(new JLabel("Nouveau statut :"));
        ligneStatut.add(listeStatuts);
        ligneStatut.add(changerStatut);

        JPanel actions = new JPanel(new GridLayout(3, 1));
        actions.add(affectation);
        actions.add(ligneStatut);
        actions.add(formulaireCreation);

        add(titreProjet, BorderLayout.NORTH);
        add(new JScrollPane(listeTaches), BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        creer.addActionListener(event -> creerTache());
        assigner.addActionListener(event -> assignerUtilisateur());
        changerStatut.addActionListener(event -> changerStatut());
        listeTaches.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                afficherResponsableSelectionne();
                afficherTransitionsDisponibles();
            }
        });
    }

    public void afficherProjet(Projet projet) {
        this.projet = projet;
        if (projet == null) {
            titreProjet.setText("Selectionnez un projet.");
            tachesModel.clear();
            return;
        }
        titreProjet.setText("Projet : " + projet.getNom());
        rafraichirTaches(null);
    }

    private void creerTache() {
        if (projet == null || projet.getId() == null) {
            afficherErreur("Selectionnez d'abord un projet.", "Création refusée");
            return;
        }

        try {
            LocalDate echeance = champEcheance.getText().isBlank()
                    ? null
                    : LocalDate.parse(champEcheance.getText().trim());
            tacheService.creerTache(projet.getId(), champTitre.getText(),
                    champDescription.getText(), echeance);
            champTitre.setText("");
            champDescription.setText("");
            champEcheance.setText("");
            rafraichirTaches(null);
        } catch (DateTimeParseException e) {
            afficherErreur("L'échéance doit être une date valide au format AAAA-MM-JJ.",
                    "Création refusée");
        } catch (IllegalArgumentException e) {
            afficherErreur(e.getMessage(), "Création refusée");
        } catch (PersistanceException e) {
            afficherErreur(e.getMessage(), "Erreur de persistance");
        }
    }

    private void assignerUtilisateur() {
        Tache tache = listeTaches.getSelectedValue();
        if (tache == null) {
            afficherErreur("Selectionnez d'abord une tâche.", "Assignation refusée");
            return;
        }

        Utilisateur utilisateur = (Utilisateur) listeUtilisateurs.getSelectedItem();
        try {
            tacheService.assignerUtilisateur(tache.getId(), utilisateur == null
                    ? null
                    : utilisateur.getId());
            rafraichirTaches(tache.getId());
        } catch (IllegalArgumentException e) {
            afficherErreur(e.getMessage(), "Assignation refusée");
        } catch (PersistanceException e) {
            afficherErreur(e.getMessage(), "Erreur de persistance");
        }
    }

    private void changerStatut() {
        Tache tache = listeTaches.getSelectedValue();
        String nouveauStatut = (String) listeStatuts.getSelectedItem();
        if (tache == null || nouveauStatut == null) {
            afficherErreur("Selectionnez une tâche et un statut autorisé.",
                    "Changement refusé");
            return;
        }

        try {
            tacheService.changerStatut(tache.getId(), nouveauStatut);
            rafraichirTaches(tache.getId());
        } catch (IllegalArgumentException e) {
            afficherErreur(e.getMessage(), "Changement refusé");
        } catch (PersistanceException e) {
            afficherErreur(e.getMessage(), "Erreur de persistance");
        }
    }

    private void chargerUtilisateurs() {
        try {
            List<Utilisateur> utilisateurs = tacheService.listerUtilisateurs();
            utilisateursModel.removeAllElements();
            utilisateursModel.addElement(null);
            nomsUtilisateurs.clear();
            for (Utilisateur utilisateur : utilisateurs) {
                utilisateursModel.addElement(utilisateur);
                nomsUtilisateurs.put(utilisateur.getId(), utilisateur.getNom());
            }
        } catch (PersistanceException e) {
            afficherErreur(e.getMessage(), "Erreur de persistance");
        }
    }

    private void rafraichirTaches(Integer tacheIdSelectionnee) {
        if (projet == null || projet.getId() == null) {
            return;
        }
        try {
            List<Tache> taches = tacheService.listerParProjet(projet.getId());
            tachesModel.clear();
            for (Tache tache : taches) {
                tachesModel.addElement(tache);
                if (tache.getId().equals(tacheIdSelectionnee)) {
                    listeTaches.setSelectedValue(tache, true);
                }
            }
            if (tacheIdSelectionnee == null && !taches.isEmpty()) {
                listeTaches.setSelectedIndex(0);
            } else if (tacheIdSelectionnee == null) {
                utilisateursModel.setSelectedItem(null);
            }
        } catch (IllegalArgumentException e) {
            afficherErreur(e.getMessage(), "Erreur");
        } catch (PersistanceException e) {
            afficherErreur(e.getMessage(), "Erreur de persistance");
        }
    }

    private void afficherResponsableSelectionne() {
        Tache tache = listeTaches.getSelectedValue();
        Integer utilisateurId = tache == null ? null : tache.getUtilisateurId();
        for (int index = 0; index < utilisateursModel.getSize(); index++) {
            Utilisateur utilisateur = utilisateursModel.getElementAt(index);
            if (utilisateurId == null && utilisateur == null
                    || utilisateur != null && utilisateurId != null
                    && utilisateurId.equals(utilisateur.getId())) {
                listeUtilisateurs.setSelectedIndex(index);
                return;
            }
        }
        if (utilisateurId != null) {
            utilisateursModel.setSelectedItem(null);
        }
    }

    private void afficherTransitionsDisponibles() {
        Tache tache = listeTaches.getSelectedValue();
        statutsModel.removeAllElements();
        changerStatut.setEnabled(false);
        if (tache == null || "terminé".equals(tache.getStatut())) {
            return;
        }
        if ("à faire".equals(tache.getStatut())) {
            statutsModel.addElement("en cours");
            statutsModel.addElement("terminé");
        } else if ("en cours".equals(tache.getStatut())) {
            statutsModel.addElement("terminé");
        }
        changerStatut.setEnabled(statutsModel.getSize() > 0);
    }

    private ListCellRenderer<Utilisateur> creerRenduUtilisateur() {
        return (list, value, index, isSelected, cellHasFocus) -> {
            JLabel etiquette = (JLabel) new DefaultListCellRenderer()
                    .getListCellRendererComponent(list,
                            value == null ? "Non assignée" : value.getNom(),
                            index, isSelected, cellHasFocus);
            return etiquette;
        };
    }

    private ListCellRenderer<Tache> creerRenduTache() {
        return (list, value, index, isSelected, cellHasFocus) -> {
            String responsable = value == null || value.getUtilisateurId() == null
                    ? "Non assignée"
                    : nomsUtilisateurs.getOrDefault(value.getUtilisateurId(), "Membre inconnu");
            String texte = value == null
                    ? ""
                    : value.getTitre() + " — " + value.getStatut()
                    + " — Responsable : " + responsable;
            Component composant = new DefaultListCellRenderer()
                    .getListCellRendererComponent(list, texte, index, isSelected, cellHasFocus);
            return composant;
        };
    }

    private void afficherErreur(String message, String titre) {
        JOptionPane.showMessageDialog(this, message, titre, JOptionPane.WARNING_MESSAGE);
    }
}
