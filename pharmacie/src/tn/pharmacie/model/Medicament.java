package tn.pharmacie.model;


public class Medicament {

    private int idMedicament;
    private String nom;
    private String dosage;
    private int stock;
    private double prixUnitaire;

    public Medicament(int idMedicament, String nom, String dosage, int stock, double prixUnitaire) {
        this.idMedicament = idMedicament;
        this.nom = nom;
        this.dosage = dosage;
        this.stock = stock;
        this.prixUnitaire = prixUnitaire;
    }

    
    public int getIdMedicament() {
        return idMedicament;
    }

    public void setIdMedicament(int idMedicament) {
        this.idMedicament = idMedicament;
    }

    public String getNom() {
        return nom;
    }


    public void setNom(String nom) {
        this.nom = nom;
    }


    public String getDosage() {
        return dosage;
    }


    public void setDosage(String dosage) {
        this.dosage = dosage;
    }


    public int getStock() {
        return stock;
    }


    public void setStock(int stock) {
        this.stock = stock;
    }


    public double getPrixUnitaire() {
        return prixUnitaire;
    }


    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }
}
