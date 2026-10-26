package tn.pharmacie.dao;

import tn.pharmacie.db.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MedicamentDao {

    // DTO / Record-like class for easy usage in services/UI
    public static class Medicament {
        private final int id;
        private final String nom;
        private final String dosage;
        private final int stock;
        private final double prixUnitaire;

        public Medicament(int id, String nom, String dosage, int stock, double prixUnitaire) {
            this.id = id;
            this.nom = nom;
            this.dosage = dosage;
            this.stock = stock;
            this.prixUnitaire = prixUnitaire;
        }

        public int id() { return id; }
        public String nom() { return nom; }
        public String dosage() { return dosage; }
        public int stock() { return stock; }
        public double prixUnitaire() { return prixUnitaire; }
    }

    public int create(String nom, String dosage, int stock, double prixUnitaire) throws Exception {
        String sql = "INSERT INTO Medicament(nom, dosage, stock, prix_unitaire) VALUES(?,?,?,?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nom);
            ps.setString(2, dosage);
            ps.setInt(3, stock);
            ps.setDouble(4, prixUnitaire);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public Medicament get(int idMedicament) throws Exception {
        String sql = "SELECT id_medicament, nom, dosage, stock, prix_unitaire FROM Medicament WHERE id_medicament=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idMedicament);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new Medicament(
                        rs.getInt("id_medicament"),
                        rs.getString("nom"),
                        rs.getString("dosage"),
                        rs.getInt("stock"),
                        rs.getDouble("prix_unitaire")
                );
            }
        }
    }

    public boolean exists(int idMedicament) throws Exception {
        String sql = "SELECT 1 FROM Medicament WHERE id_medicament=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idMedicament);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Medicament> listAll() throws Exception {
        String sql = "SELECT id_medicament, nom, dosage, stock, prix_unitaire FROM Medicament ORDER BY nom";
        List<Medicament> out = new ArrayList<>();

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(new Medicament(
                        rs.getInt("id_medicament"),
                        rs.getString("nom"),
                        rs.getString("dosage"),
                        rs.getInt("stock"),
                        rs.getDouble("prix_unitaire")
                ));
            }
        }
        return out;
    }

    // stock <= seuil
    public List<Medicament> listCritical(int seuil) throws Exception {
        String sql = "SELECT id_medicament, nom, dosage, stock, prix_unitaire " +
                     "FROM Medicament WHERE stock <= ? ORDER BY stock ASC";
        List<Medicament> out = new ArrayList<>();

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, seuil);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new Medicament(
                            rs.getInt("id_medicament"),
                            rs.getString("nom"),
                            rs.getString("dosage"),
                            rs.getInt("stock"),
                            rs.getDouble("prix_unitaire")
                    ));
                }
            }
        }
        return out;
    }

    // Set stock to exact value
    public int updateStock(int idMedicament, int newStock) throws Exception {
        String sql = "UPDATE Medicament SET stock=? WHERE id_medicament=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, newStock);
            ps.setInt(2, idMedicament);
            return ps.executeUpdate();
        }
    }

    // Change stock by delta (+/-)
    public int changeStock(int idMedicament, int delta) throws Exception {
        String sql = "UPDATE Medicament SET stock = stock + ? WHERE id_medicament=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, idMedicament);
            return ps.executeUpdate();
        }
    }

    public int updatePrice(int idMedicament, double newPrixUnitaire) throws Exception {
        String sql = "UPDATE Medicament SET prix_unitaire=? WHERE id_medicament=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, newPrixUnitaire);
            ps.setInt(2, idMedicament);
            return ps.executeUpdate();
        }
    }

    /**
     * Delete a medicament.
     * @return number of rows deleted (0 if not found)
     */
    public int delete(int idMedicament) throws Exception {
        String sql = "DELETE FROM Medicament WHERE id_medicament=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idMedicament);
            return ps.executeUpdate();
        }
    }
}
