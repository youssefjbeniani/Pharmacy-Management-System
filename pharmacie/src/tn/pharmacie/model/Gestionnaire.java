package tn.pharmacie.model;


public class Gestionnaire {

    private int idGestionnaire;
    private String nom;
    private String prenom;
    private String login;
    private String pwd;

    public Gestionnaire(int idGestionnaire, String nom, String prenom, String login, String pwd) {
        this.idGestionnaire = idGestionnaire;
        this.nom = nom;
        this.prenom = prenom;
        this.login = login;
        this.pwd = pwd;
    }
    public int getIdGestionnaire() { 
        return idGestionnaire; 
    }


    public void setIdGestionnaire(int idGestionnaire) { 
        this.idGestionnaire = idGestionnaire; 
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

    public String getLogin() { 
        return login; 
    }


    public void setLogin(String login) { 
        this.login = login; 
    }


    public String getPwd() { 
        return pwd; 
    }


    public void setPwd(String pwd) { 
        this.pwd = pwd; 
    }
}
