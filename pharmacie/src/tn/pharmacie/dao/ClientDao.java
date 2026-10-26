package tn.pharmacie.dao;

import tn.pharmacie.db.Database;     

import java.sql.Connection;         
import java.sql.PreparedStatement;    
import java.sql.ResultSet;            
import java.sql.Statement;           
import java.util.ArrayList;           
import java.util.List;                

public class ClientDao {

   
    
    public static class Client {

        
        private final int id;
        private final String nom;
        private final String prenom;
        private final String email;
        private final String adresse;

        
        public Client(int id, String nom, String prenom, String email, String adresse) {
            this.id = id;
            this.nom = nom;
            this.prenom = prenom;
            this.email = email;
            this.adresse = adresse;
        }

        
        public int id() { return id; }
        public String nom() { return nom; }
        public String prenom() { return prenom; }
        public String email() { return email; }
        public String adresse() { return adresse; }
    }


    
    public int create(String nom, String prenom, String email, String adresse) throws Exception {

        String sql = "INSERT INTO Client(nom, prenom, email, adresse) VALUES(?,?,?,?)";

        
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nom);
            ps.setString(2, prenom);
            ps.setString(3, email);
            ps.setString(4, adresse);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }


   
    public Client get(int idClient) throws Exception {

        String sql = "SELECT id_client, nom, prenom, email, adresse FROM Client WHERE id_client=?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idClient);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) return null;

                return new Client(
                        rs.getInt("id_client"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("adresse")
                );
            }
        }
    }


    
    public boolean exists(int idClient) throws Exception {

        String sql = "SELECT 1 FROM Client WHERE id_client=?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idClient);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

  
   
    public List<Client> listAll() throws Exception {

        String sql = "SELECT id_client, nom, prenom, email, adresse FROM Client ORDER BY nom, prenom";

        List<Client> out = new ArrayList<>();

        
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(new Client(
                        rs.getInt("id_client"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("adresse")
                ));
            }
        }
        return out;
    }

 
   
    public List<Client> searchByName(String keyword) throws Exception {

       
        String sql = "SELECT id_client, nom, prenom, email, adresse " +
                     "FROM Client WHERE nom LIKE ? OR prenom LIKE ? " +
                     "ORDER BY nom, prenom";

       
        String like = "%" + keyword + "%";

        List<Client> out = new ArrayList<>();

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

           
            ps.setString(1, like);
            ps.setString(2, like);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new Client(
                            rs.getInt("id_client"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("adresse")
                    ));
                }
            }
        }
        return out;
    }

 
    public void update(int idClient, String nom, String prenom, String email, String adresse) throws Exception {

        String sql = "UPDATE Client SET nom=?, prenom=?, email=?, adresse=? WHERE id_client=?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nom);
            ps.setString(2, prenom);
            ps.setString(3, email);
            ps.setString(4, adresse);
            ps.setInt(5, idClient);

           
            ps.executeUpdate();
        }
    }

   
  
    public void delete(int idClient) throws Exception {

        String sql = "DELETE FROM Client WHERE id_client=?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idClient);

            ps.executeUpdate();
        }
    }
}
