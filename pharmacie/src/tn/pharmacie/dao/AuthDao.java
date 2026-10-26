package tn.pharmacie.dao;

import tn.pharmacie.db.Database;      
import tn.pharmacie.model.Session;   

import java.sql.Connection;           
import java.sql.PreparedStatement;   
import java.sql.ResultSet;            


 
public class AuthDao {

   
    public Session login(String login, String pwd) {

       
        String sqlP = "SELECT id_pharmacien, nom, prenom FROM Pharmacien WHERE login=? AND pwd=?";

        
        String sqlG = "SELECT id_gestionnaire, nom, prenom FROM Gestionnaire WHERE login=? AND pwd=?";

        
        try (Connection c = Database.getConnection()) {

           
            try (PreparedStatement ps = c.prepareStatement(sqlP)) {

                
                ps.setString(1, login); 
                ps.setString(2, pwd);   

               
                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {

                       
                        return new Session(
                                "PHARMACIEN",
                                rs.getInt(1),
                                rs.getString("nom"),
                                rs.getString("prenom")
                        );
                    }
                }
            }

      
            
            try (PreparedStatement ps = c.prepareStatement(sqlG)) {

                ps.setString(1, login);
                ps.setString(2, pwd);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {

                       
                        return new Session(
                                "GESTIONNAIRE",
                                rs.getInt(1),
                                rs.getString("nom"),
                                rs.getString("prenom")
                        );
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur login: " + e.getMessage());
        }

        return null;
    }
}
