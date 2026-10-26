package tn.pharmacie.dao;

import tn.pharmacie.db.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * VenteDao
 * --------
 * Accès JDBC à la table Vente (CRUD + recherches).
 */
public class VenteDao {

    // Petit DTO (record) pour transporter une vente
    public record Vente(int idVente, int idPharmacien, int idClient, int idMedicament, int quantite, String date) {}

    /**
     * Crée une vente.
     * IMPORTANT : id_pharmacien est NOT NULL dans le schéma => obligatoire.
     *
     * @return id_vente généré
     */
    public int create(int idPharmacien, int idClient, int idMedicament, int quantite, String dateIso) throws Exception {
        String sql = """
                INSERT INTO Vente(id_pharmacien, id_client, id_medicament, quantite, date)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, idPharmacien);
            ps.setInt(2, idClient);
            ps.setInt(3, idMedicament);
            ps.setInt(4, quantite);
            ps.setString(5, dateIso);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Impossible de récupérer l'ID de la vente.");
    }

    /**
     * Récupère une vente par id.
     */
    public Vente get(int idVente) throws Exception {
        String sql = """
                SELECT id_vente, id_pharmacien, id_client, id_medicament, quantite, date
                FROM Vente
                WHERE id_vente = ?
                """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idVente);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Vente(
                            rs.getInt("id_vente"),
                            rs.getInt("id_pharmacien"),
                            rs.getInt("id_client"),
                            rs.getInt("id_medicament"),
                            rs.getInt("quantite"),
                            rs.getString("date")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Supprime une vente.
     * @return nb de lignes supprimées
     */
    public int delete(int idVente) throws Exception {
        String sql = "DELETE FROM Vente WHERE id_vente = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idVente);
            return ps.executeUpdate();
        }
    }

    public List<Vente> listByMedicament(int idMedicament) throws Exception {
        String sql = """
                SELECT id_vente, id_pharmacien, id_client, id_medicament, quantite, date
                FROM Vente
                WHERE id_medicament = ?
                ORDER BY date DESC
                """;
        return listWithOneInt(sql, idMedicament);
    }

    public List<Vente> listByClient(int idClient) throws Exception {
        String sql = """
                SELECT id_vente, id_pharmacien, id_client, id_medicament, quantite, date
                FROM Vente
                WHERE id_client = ?
                ORDER BY date DESC
                """;
        return listWithOneInt(sql, idClient);
    }

    public List<Vente> listByPeriode(String dateDebutIso, String dateFinIso) throws Exception {
        String sql = """
                SELECT id_vente, id_pharmacien, id_client, id_medicament, quantite, date
                FROM Vente
                WHERE date >= ? AND date <= ?
                ORDER BY date DESC
                """;

        List<Vente> res = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, dateDebutIso);
            ps.setString(2, dateFinIso);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) res.add(map(rs));
            }
        }
        return res;
    }

    public List<Vente> listAll() throws Exception {
        String sql = """
                SELECT id_vente, id_pharmacien, id_client, id_medicament, quantite, date
                FROM Vente
                ORDER BY date DESC
                """;

        List<Vente> res = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) res.add(map(rs));
        }
        return res;
    }

    // ---------- helpers ----------
    private List<Vente> listWithOneInt(String sql, int value) throws Exception {
        List<Vente> res = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) res.add(map(rs));
            }
        }
        return res;
    }

    private Vente map(ResultSet rs) throws SQLException {
        return new Vente(
                rs.getInt("id_vente"),
                rs.getInt("id_pharmacien"),
                rs.getInt("id_client"),
                rs.getInt("id_medicament"),
                rs.getInt("quantite"),
                rs.getString("date")
        );
    }
}

