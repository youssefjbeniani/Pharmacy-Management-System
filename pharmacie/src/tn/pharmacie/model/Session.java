package tn.pharmacie.model;


public class Session {

    private String role;

    private int userId;
    private String nom;
    private String prenom;

    public Session(String role, int userId, String nom, String prenom) {
        this.role = role;
        this.userId = userId;
        this.nom = nom;
        this.prenom = prenom;
    }

    public String getRole() {
        return role;
    }


    public int getUserId() {
        return userId;
    }


    public String getNom() {
        return nom;
    }


    public String getPrenom() {
        return prenom;
    }

    public String nomComplet() {
        return prenom + " " + nom;
    }
}
