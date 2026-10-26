package tn.pharmacie.service;

import tn.pharmacie.dao.ClientDao;
import tn.pharmacie.dao.MedicamentDao;
import tn.pharmacie.dao.StockHistoriqueDao;
import tn.pharmacie.dao.VenteDao;
import tn.pharmacie.model.Session;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * PharmacienService
 * -----------------
 * Couche métier pour les actions du Pharmacien.
 *
 * Responsabilités :
 * - Enregistrer/Annuler une vente
 * - Vérifier la validité des données (quantité, existence client/médicament)
 * - Mettre à jour le stock du médicament
 * - Écrire l'historique de stock
 * - Fournir les consultations de ventes
 *
 * NOTE : Cette classe utilise uniquement des DAO (JDBC) pour accéder à la base.
 */
public class PharmacienService {

    /**
     * Seuil d'alerte stock bas (tu peux le changer).
     * Exemple : si stock <= 5 => alerte.
     */
    private static final int STOCK_LOW_THRESHOLD = 5;

    private final Session session;

    private final ClientDao clientDao = new ClientDao();
    private final MedicamentDao medicamentDao = new MedicamentDao();
    private final VenteDao venteDao = new VenteDao();
    private final StockHistoriqueDao stockHistoriqueDao = new StockHistoriqueDao();

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public PharmacienService(Session session) {
        if (session == null) throw new IllegalArgumentException("Session invalide.");
        this.session = session;
    }

    private String nowIso() {
        return LocalDateTime.now().format(ISO);
    }

    // ============================================================
    // 1) Enregistrer une vente
    // ============================================================

    /**
     * Enregistre une vente :
     * 1) Vérifie client + médicament
     * 2) Vérifie stock suffisant
     * 3) INSERT Vente (avec id_pharmacien NOT NULL)
     * 4) Décrémente stock du médicament
     * 5) Log historique stock (delta négatif)
     * 6) Affiche alerte si stock bas
     *
     * @return idVente créé
     */
    public int enregistrerVente(int idClient, int idMedicament, int quantite) throws Exception {
        if (idClient <= 0) throw new IllegalArgumentException("ID client invalide.");
        if (idMedicament <= 0) throw new IllegalArgumentException("ID medicament invalide.");
        if (quantite <= 0) throw new IllegalArgumentException("Quantité invalide.");

        // 1) Vérifier existence client
        if (!clientDao.exists(idClient)) {
            throw new IllegalArgumentException("Client introuvable (id=" + idClient + ").");
        }

        // 2) Vérifier existence médicament + stock
        MedicamentDao.Medicament med = medicamentDao.get(idMedicament);
        if (med == null) {
            throw new IllegalArgumentException("Médicament introuvable (id=" + idMedicament + ").");
        }
        if (med.stock() < quantite) {
            throw new IllegalArgumentException("Stock insuffisant. Stock actuel=" + med.stock());
        }

        // 3) Créer la vente (id_pharmacien vient de la session)
        int idVente = venteDao.create(session.getUserId(), idClient, idMedicament, quantite, nowIso());

        // 4) Décrémenter le stock (delta négatif)
        int delta = -quantite;
        medicamentDao.changeStock(idMedicament, delta);

        // 5) Historique stock
        stockHistoriqueDao.log(idMedicament, delta, nowIso());

        // 6) Alerte stock bas (recharger le stock)
        MedicamentDao.Medicament medAfter = medicamentDao.get(idMedicament);
        if (medAfter != null && medAfter.stock() <= STOCK_LOW_THRESHOLD) {
            System.out.println("⚠️ ALERTE: Stock bas pour [" + medAfter.nom() + " - " + medAfter.dosage()
                    + "] stock=" + medAfter.stock() + " (seuil=" + STOCK_LOW_THRESHOLD + ")");
        }

        return idVente;
    }

    // ============================================================
    // 2) Annuler une vente
    // ============================================================

    /**
     * Annule une vente :
     * 1) Récupère la vente
     * 2) Restock (+quantite)
     * 3) Supprime la vente
     * 4) Log historique stock (delta positif)
     */
    public void annulerVente(int idVente) throws Exception {
        if (idVente <= 0) throw new IllegalArgumentException("ID vente invalide.");

        VenteDao.Vente v = venteDao.get(idVente);
        if (v == null) {
            throw new IllegalArgumentException("Vente introuvable (id=" + idVente + ").");
        }

        // Restock
        int delta = v.quantite(); // +quantite
        medicamentDao.changeStock(v.idMedicament(), delta);

        // Delete vente
        int deleted = venteDao.delete(idVente);
        if (deleted == 0) {
            throw new IllegalStateException("Annulation échouée: vente non supprimée.");
        }

        // Historique stock
        stockHistoriqueDao.log(v.idMedicament(), delta, nowIso());
    }

    // ============================================================
    // 3) Consultations
    // ============================================================

    public List<VenteDao.Vente> ventesParMedicament(int idMedicament) throws Exception {
        if (idMedicament <= 0) throw new IllegalArgumentException("ID medicament invalide.");
        return venteDao.listByMedicament(idMedicament);
    }

    public List<VenteDao.Vente> ventesParClient(int idClient) throws Exception {
        if (idClient <= 0) throw new IllegalArgumentException("ID client invalide.");
        return venteDao.listByClient(idClient);
    }

    public List<VenteDao.Vente> ventesParPeriode(String dateDebutIso, String dateFinIso) throws Exception {
        if (dateDebutIso == null || dateDebutIso.isBlank()) throw new IllegalArgumentException("Date début invalide.");
        if (dateFinIso == null || dateFinIso.isBlank()) throw new IllegalArgumentException("Date fin invalide.");
        return venteDao.listByPeriode(dateDebutIso, dateFinIso);
    }

    // ============================================================
    // 4) Créer un client
    // ============================================================

    /**
     * Crée un client en base.
     * @return id_client généré
     */
    public int creerClient(String nom, String prenom, String email, String adresse) throws Exception {
        if (nom == null || nom.isBlank()) throw new IllegalArgumentException("Nom invalide.");
        if (prenom == null || prenom.isBlank()) throw new IllegalArgumentException("Prénom invalide.");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email invalide.");
        if (adresse == null) adresse = "";

        return clientDao.create(nom.trim(), prenom.trim(), email.trim(), adresse.trim());
    }
}
