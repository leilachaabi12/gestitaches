package fr.usms.gestitaches.model;

import java.time.LocalDate;

public class Tache {

    private final Integer id;
    private final String titre;
    private final String description;
    private final String statut;
    private final LocalDate echeance;
    private final LocalDate dateLimite;
    private final Integer utilisateurId;
    private final int projetId;

    public Tache(Integer id, String titre, String description, String statut,
                 LocalDate echeance, LocalDate dateLimite, Integer utilisateurId, int projetId) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.statut = statut;
        this.echeance = echeance;
        this.dateLimite = dateLimite;
        this.utilisateurId = utilisateurId;
        this.projetId = projetId;
    }

    public Integer getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description;
    }

    public String getStatut() {
        return statut;
    }

    public LocalDate getEcheance() {
        return echeance;
    }

    public LocalDate getDateLimite() {
        return dateLimite;
    }

    public Integer getUtilisateurId() {
        return utilisateurId;
    }

    public int getProjetId() {
        return projetId;
    }

    @Override
    public String toString() {
        return titre + " — " + statut;
    }
}
