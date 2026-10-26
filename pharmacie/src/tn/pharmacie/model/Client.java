package tn.pharmacie.model;


public class Client {

    private int idClient;
    private String nom;
    private String prenom;
    private String email;
    private String adresse;


    public Client(int idClient, String nom, String prenom, String email, String adresse) {
        this.idClient = idClient;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.adresse = adresse;
    }

  
    public int getIdClient() { 
        return idClient; 
    }

    public void setIdClient(int idClient) { 
        this.idClient = idClient; 
    }

    public String getNom() { 
        return nom;
    }

   
    public void setNom(String nom) { 
        this.nom = nom; 
    }

    public String getPrenom() { 
        return prenom; 
    }

    
    public void setPrenom(String prenom) {
        this.prenom = prenom; 
    }

   
    public String getEmail() { 
        return email; 
    }

  
    public void setEmail(String email) { 
        this.email = email; 
    }

   
    public String getAdresse() { 
        return adresse; 
    }

    
    public void setAdresse(String adresse) { 
        this.adresse = adresse; 
    }
}
