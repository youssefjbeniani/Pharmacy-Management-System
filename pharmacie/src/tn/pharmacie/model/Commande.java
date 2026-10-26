package tn.pharmacie.model;


public class Commande {

    private int idCommande;
    private int idGestionnaire;
    private int idMedicament;
    private int quantite;
    private String date; // ISO string

    public Commande(int idCommande, int idGestionnaire, int idMedicament, int quantite, String date) {
        this.idCommande = idCommande;
        this.idGestionnaire = idGestionnaire;
        this.idMedicament = idMedicament;
        this.quantite = quantite;
        this.date = date;
    }

   
    public int getIdCommande() {
        return idCommande;
    }

   
    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
    }

   
    public int getIdGestionnaire() {
        return idGestionnaire;
    }

   
    public void setIdGestionnaire(int idGestionnaire) {
        this.idGestionnaire = idGestionnaire;
    }


    public int getIdMedicament() {
        return idMedicament;
    }

    public void setIdMedicament(int idMedicament) {
        this.idMedicament = idMedicament;
    }


    public int getQuantite() {
        return quantite;
    }


    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }


    public String getDate() {
        return date;
    }


    public void setDate(String date) {
        this.date = date;
    }
}
