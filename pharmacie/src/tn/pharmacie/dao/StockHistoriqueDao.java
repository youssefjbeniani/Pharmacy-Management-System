package tn.pharmacie.dao;

import tn.pharmacie.db.Database;     
import java.sql.*;                  
import java.util.ArrayList;         
import java.util.List;             


public class StockHistoriqueDao {

    
    public record StockHist(int id, int idMedicament, int quantite, String date) {}

   
    public void log(int idMed, int quantite, String dateIso) throws Exception {

        String sql = "INSERT INTO StockHistorique(id_medicament, quantite, date) VALUES(?,?,?)";

       
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idMed);       
            ps.setInt(2, quantite);    
            ps.setString(3, dateIso);  

            ps.executeUpdate();
        }
    }

  
    public List<StockHist> listByMedicament(int idMed) throws Exception {

        String sql = "SELECT id_stock_hist, id_medicament, quantite, date " +
                     "FROM StockHistorique WHERE id_medicament=? ORDER BY date DESC";

        List<StockHist> out = new ArrayList<>();

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idMed);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                   
                    out.add(new StockHist(
                            rs.getInt(1),
                            rs.getInt(2),
                            rs.getInt(3),
                            rs.getString(4)
                    ));
                }
            }
        }

        return out;
    }
}
