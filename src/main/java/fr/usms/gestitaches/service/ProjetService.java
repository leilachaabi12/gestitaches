package fr.usms.gestitaches.service;

import fr.usms.gestitaches.dao.PersistanceException;
import fr.usms.gestitaches.dao.ProjetDAO;
import fr.usms.gestitaches.model.Projet;

import java.util.List;

public class ProjetService {

    private final ProjetDAO projetDAO;

    public ProjetService(ProjetDAO projetDAO) {
        this.projetDAO = projetDAO;
    }

    public Projet creerProjet(String nom) {
        return creerProjet(nom, null);
    }

    public Projet creerProjet(String nom, String description) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du projet est obligatoire.");
        }
        String nomNormalise = nom.trim();
        String descriptionNormalisee = description == null || description.trim().isEmpty()
                ? null
                : description.trim();
        if (projetDAO.existeParNom(nomNormalise)) {
            throw new IllegalArgumentException("Ce projet existe deja.");
        }

        return projetDAO.creer(nomNormalise, descriptionNormalisee);
    }

    public List<Projet> listerProjets() {
        return projetDAO.trouverTous();
    }

    public Projet consulterProjet(int id) {
        return projetDAO.trouverParId(id);
    }
}
