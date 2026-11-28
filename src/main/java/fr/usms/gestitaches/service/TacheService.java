package fr.usms.gestitaches.service;

import fr.usms.gestitaches.dao.ProjetDAO;
import fr.usms.gestitaches.dao.TacheDAO;
import fr.usms.gestitaches.model.Projet;
import fr.usms.gestitaches.model.Tache;

import java.time.LocalDate;
import java.util.List;

public class TacheService {

    private static final String STATUT_INITIAL = "à faire";

    private final TacheDAO tacheDAO;
    private final ProjetDAO projetDAO;

    public TacheService(TacheDAO tacheDAO, ProjetDAO projetDAO) {
        this.tacheDAO = tacheDAO;
        this.projetDAO = projetDAO;
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
}
