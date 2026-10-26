package tn.pharmacie.service;

import tn.pharmacie.dao.CommandeDao;
import tn.pharmacie.dao.MedicamentDao;
import tn.pharmacie.dao.StockHistoriqueDao;
import tn.pharmacie.model.Session;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GestionnaireService {

    private final Session session;

    private final MedicamentDao medicamentDao = new MedicamentDao();
    private final CommandeDao commandeDao = new CommandeDao();
    private final StockHistoriqueDao stockHistoriqueDao = new StockHistoriqueDao();

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public GestionnaireService(Session session) {
        this.session = session;
    }

    private String nowIso() {
        return LocalDateTime.now().format(ISO);
    }

    // 1) Ajouter un médicament + log stock initial
    public int ajouterMedicament(String nom, String dosage, int stockInitial, double prixUnitaire) throws Exception {
        if (nom == null || nom.trim().isEmpty()) throw new IllegalArgumentException("Nom médicament invalide.");
        if (dosage == null || dosage.trim().isEmpty()) throw new IllegalArgumentException("Dosage invalide.");
        if (stockInitial < 0) throw new IllegalArgumentException("Stock initial invalide.");
        if (prixUnitaire < 0) throw new IllegalArgumentException("Prix invalide.");

        int id = medicamentDao.create(nom.trim(), dosage.trim(), stockInitial, prixUnitaire);
        stockHistoriqueDao.log(id, stockInitial, nowIso());
        return id;
    }

    // 2) Mettre à jour stock (valeur exacte) + log delta
    public void mettreAJourStockExact(int idMedicament, int nouveauStock) throws Exception {
        if (nouveauStock < 0) throw new IllegalArgumentException("Stock invalide.");

        MedicamentDao.Medicament med = medicamentDao.get(idMedicament);
        if (med == null) throw new IllegalArgumentException("Médicament introuvable (id=" + idMedicament + ").");

        int delta = nouveauStock - med.stock();

        medicamentDao.updateStock(idMedicament, nouveauStock);
        stockHistoriqueDao.log(idMedicament, delta, nowIso());
    }

    // 3) Stock critique
    public List<MedicamentDao.Medicament> stockCritique(int seuil) throws Exception {
        if (seuil < 0) throw new IllegalArgumentException("Seuil invalide.");
        return medicamentDao.listCritical(seuil);
    }

    // 4) Créer commande
    public int creerCommande(int idMedicament, int quantite) throws Exception {
        if (quantite <= 0) throw new IllegalArgumentException("Quantité invalide.");

        MedicamentDao.Medicament med = medicamentDao.get(idMedicament);
        if (med == null) throw new IllegalArgumentException("Médicament introuvable (id=" + idMedicament + ").");

        return commandeDao.create(session.getUserId(), idMedicament, quantite, nowIso());
    }

    // 5) Consulter commandes
    public List<CommandeDao.Commande> consulterCommandes() throws Exception {
        return commandeDao.listAll();
    }

    // 6) Historique stock
    public List<StockHistoriqueDao.StockHist> historiqueStock(int idMedicament) throws Exception {
        return stockHistoriqueDao.listByMedicament(idMedicament);
    }

    // 7) Lister tous les médicaments
    public List<MedicamentDao.Medicament> listMedicaments() throws Exception {
        return medicamentDao.listAll();
    }

    // 8) Modifier prix
    public void modifierPrix(int idMedicament, double nouveauPrix) throws Exception {
        if (nouveauPrix < 0) throw new IllegalArgumentException("Prix invalide.");
        if (!medicamentDao.exists(idMedicament)) throw new IllegalArgumentException("Médicament introuvable.");
        medicamentDao.updatePrice(idMedicament, nouveauPrix);
    }

    // 9) Supprimer médicament
    public void supprimerMedicament(int idMedicament) throws Exception {
        if (!medicamentDao.exists(idMedicament)) {
            throw new IllegalArgumentException("Médicament introuvable (id=" + idMedicament + ").");
        }

        // IMPORTANT: may fail if referenced by Vente/Commande/StockHistorique (foreign keys)
        int deleted = medicamentDao.delete(idMedicament);
        if (deleted == 0) {
            throw new IllegalStateException("Suppression échouée (aucune ligne supprimée).");
        }
    }
}
