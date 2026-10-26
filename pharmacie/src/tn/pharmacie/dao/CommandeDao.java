package tn.pharmacie.dao;

import tn.pharmacie.db.Database;     

import java.sql.*;                  
import java.util.ArrayList;          
import java.util.List;               


public class CommandeDao {

   
    
    public record Commande(int id, int idGestionnaire, int idMedicament, int quantite, String date) {}

    
    public int create(int idGestionnaire, int idMedicament, int quantite, String dateIso) throws Exception {

      
        String sql = "INSERT INTO Commande(id_gestionnaire, id_medicament, quantite, date) VALUES(?,?,?,?)";

       
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

          
            ps.setInt(1, idGestionnaire); 
            ps.setInt(2, idMedicament);   
            ps.setInt(3, quantite);       
            ps.setString(4, dateIso);     

          
            ps.executeUpdate();

           
            try (ResultSet rs = ps.getGeneratedKeys()) {
               
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    
    public List<Commande> listAll() throws Exception {

       
        String sql = "SELECT id_commande, id_gestionnaire, id_medicament, quantite, date FROM Commande ORDER BY date DESC";

       
        List<Commande> out = new ArrayList<>();

       
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            
            while (rs.next()) {

               
                out.add(new Commande(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getString(5)
                ));
            }
        }

       
        return out;
    }
}
