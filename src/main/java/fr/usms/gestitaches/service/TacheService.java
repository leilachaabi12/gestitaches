package fr.usms.gestitaches.service;

import fr.usms.gestitaches.dao.ProjetDAO;
import fr.usms.gestitaches.dao.TacheDAO;
import fr.usms.gestitaches.dao.UtilisateurDAO;
import fr.usms.gestitaches.model.Projet;
import fr.usms.gestitaches.model.Tache;
import fr.usms.gestitaches.model.Utilisateur;

import java.time.LocalDate;
import java.util.List;

public class TacheService {

    private static final String STATUT_INITIAL = "à faire";

    private final TacheDAO tacheDAO;
    private final ProjetDAO projetDAO;
    private final UtilisateurDAO utilisateurDAO;

    public TacheService(TacheDAO tacheDAO, ProjetDAO projetDAO) {
        this(tacheDAO, projetDAO, new UtilisateurDAO());
    }

    public TacheService(TacheDAO tacheDAO, ProjetDAO projetDAO,
                        UtilisateurDAO utilisateurDAO) {
        this.tacheDAO = tacheDAO;
        this.projetDAO = projetDAO;
        this.utilisateurDAO = utilisateurDAO;
    }

    public Tache creerTache(int projetId, String titre, String description, LocalDate echeance) {
        return creerTache(projetId, titre, description, echeance, STATUT_INITIAL);
    }

    public Tache creerTache(int projetId, String titre, String description, LocalDate echeance,
                            String statutInitial) {
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre de la tâche est obligatoire.");
        }
        if (!STATUT_INITIAL.equals(statutInitial)) {
            throw new IllegalArgumentException("Une nouvelle tâche doit être au statut « à faire ».");
        }
        Projet projet = projetDAO.trouverParId(projetId);
        if (projet == null) {
            throw new IllegalArgumentException("Le projet sélectionné n'existe pas.");
        }

        String titreNormalise = titre.trim();
        String descriptionNormalisee = description == null || description.trim().isEmpty()
                ? null
                : description.trim();
        Tache nouvelleTache = new Tache(null, titreNormalise, descriptionNormalisee,
                STATUT_INITIAL, echeance, null, null, projetId);
        return tacheDAO.creer(nouvelleTache);
    }

    public List<Tache> listerParProjet(int projetId) {
        if (projetDAO.trouverParId(projetId) == null) {
            throw new IllegalArgumentException("Le projet sélectionné n'existe pas.");
        }
        return tacheDAO.trouverParProjet(projetId);
    }

    public List<Utilisateur> listerUtilisateurs() {
        return utilisateurDAO.trouverTous();
    }

    public Tache assignerUtilisateur(int tacheId, Integer utilisateurId) {
        Tache tache = tacheDAO.trouverParId(tacheId);
        if (tache == null) {
            throw new IllegalArgumentException("La tâche sélectionnée n'existe pas.");
        }
        if (utilisateurId != null && utilisateurDAO.trouverParId(utilisateurId) == null) {
            throw new IllegalArgumentException("Le membre sélectionné n'existe pas.");
        }
        tacheDAO.assignerUtilisateur(tacheId, utilisateurId);
        return tacheDAO.trouverParId(tacheId);
    }
}
