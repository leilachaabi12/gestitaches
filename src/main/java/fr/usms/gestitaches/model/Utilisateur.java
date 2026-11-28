package fr.usms.gestitaches.model;

public class Utilisateur {

    private final Integer id;
    private final String nom;
    private final String email;

    public Utilisateur(Integer id, String nom, String email) {
        this.id = id;
        this.nom = nom;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return nom;
    }
}
