package fr.usms.gestitaches.model;

public class Projet {

    private final Integer id;
    private final String nom;
    private final String description;

    public Projet(Integer id, String nom, String description) {
        this.id = id;
        this.nom = nom;
        this.description = description;
    }

    public Projet(String nom) {
        this(null, nom, null);
    }

    public Integer getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return nom;
    }
}
