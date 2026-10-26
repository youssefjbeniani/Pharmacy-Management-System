package tn.pharmacie;

import tn.pharmacie.dao.CommandeDao;
import tn.pharmacie.dao.MedicamentDao;
import tn.pharmacie.dao.StockHistoriqueDao;
import tn.pharmacie.dao.VenteDao;
import tn.pharmacie.model.Session;
import tn.pharmacie.service.AuthService;
import tn.pharmacie.service.GestionnaireService;
import tn.pharmacie.service.PharmacienService;

import java.util.List;
import java.util.Scanner;

public class App {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== PHARMACIE APP (JDBC + SQLite) ===");

        AuthService authService = new AuthService();

        while (true) {
            System.out.println();
            System.out.println("----- LOGIN -----");
            String login = readLine("Login: ");
            String pwd = readLine("Password: ");

            Session session;
            try {
                session = authService.login(login, pwd);
            } catch (Exception e) {
                System.out.println("❌ Erreur login: " + e.getMessage());
                continue;
            }

            if (session == null) {
                System.out.println("❌ Login incorrect.");
                continue;
            }

            System.out.println("✅ Connecté: " + session.getRole() + " | " + session.getPrenom() + " " + session.getNom());

            // Role routing
            String role = session.getRole() == null ? "" : session.getRole().toLowerCase();
            if (role.contains("pharmac")) {
                menuPharmacien(session);
            } else if (role.contains("gestion")) {
                menuGestionnaire(session);
            } else {
                System.out.println("⚠️ Rôle inconnu: " + session.getRole());
            }

            System.out.println("🔒 Déconnecté.");
        }
    }

    // ---------------------- Menus ----------------------

    private static void menuPharmacien(Session session) {
        PharmacienService service = new PharmacienService(session);

        while (true) {
            System.out.println();
            System.out.println("===== MENU PHARMACIEN =====");
            System.out.println("1) Enregistrer une vente");
            System.out.println("2) Annuler une vente");
            System.out.println("3) Consulter ventes par médicament");
            System.out.println("4) Consulter ventes par client");
            System.out.println("5) Consulter ventes par période");
            System.out.println("6) Créer un client");
            System.out.println("0) Logout");

            int choix = readInt("Choix: ");

            switch (choix) {
                case 1 -> {
                    try {
                        int idClient = readInt("ID client: ");
                        int idMed = readInt("ID medicament: ");
                        int qte = readInt("Quantité: ");
                        int idVente = service.enregistrerVente(idClient, idMed, qte);
                        System.out.println("✅ Vente enregistrée. ID Vente = " + idVente);
                    } catch (Exception e) {
                        printError(e);
                    }
                }
                case 2 -> {
                    try {
                        int idVente = readInt("ID vente à annuler: ");
                        service.annulerVente(idVente);
                        System.out.println("✅ Vente annulée.");
                    } catch (Exception e) {
                        printError(e);
                    }
                }
                case 3 -> {
                    try {
                        int idMed = readInt("ID medicament: ");
                        List<VenteDao.Vente> ventes = service.ventesParMedicament(idMed);
                        if (ventes.isEmpty()) {
                            System.out.println("(Aucune vente)");
                        } else {
                            for (VenteDao.Vente v : ventes) {
                                System.out.println("Vente#" + v.idVente()
                                        + " | client=" + v.idClient()
                                        + " | qte=" + v.quantite()
                                        + " | date=" + v.date());
                            }
                        }
                    } catch (Exception e) {
                        printError(e);
                    }
                }
                case 4 -> {
                    try {
                        int idClient = readInt("ID client: ");
                        List<VenteDao.Vente> ventes = service.ventesParClient(idClient);
                        if (ventes.isEmpty()) {
                            System.out.println("(Aucune vente)");
                        } else {
                            for (VenteDao.Vente v : ventes) {
                                System.out.println("Vente#" + v.idVente()
                                        + " | med=" + v.idMedicament()
                                        + " | qte=" + v.quantite()
                                        + " | date=" + v.date());
                            }
                        }
                    } catch (Exception e) {
                        printError(e);
                    }
                }
                case 5 -> {
                    try {
                        System.out.println("Format ISO attendu: 2026-01-26T14:30:00");
                        String start = readLine("Date début: ");
                        String end = readLine("Date fin  : ");
                        List<VenteDao.Vente> ventes = service.ventesParPeriode(start, end);
                        if (ventes.isEmpty()) {
                            System.out.println("(Aucune vente)");
                        } else {
                            for (VenteDao.Vente v : ventes) {
                                System.out.println("Vente#" + v.idVente()
                                        + " | med=" + v.idMedicament()
                                        + " | client=" + v.idClient()
                                        + " | qte=" + v.quantite()
                                        + " | date=" + v.date());
                            }
                        }
                    } catch (Exception e) {
                        printError(e);
                    }
                }
                case 6 -> {
                    try {
                        String nom = readLine("Nom: ");
                        String prenom = readLine("Prénom: ");
                        String email = readLine("Email: ");
                        String adresse = readLine("Adresse: ");
                        int idClient = service.creerClient(nom, prenom, email, adresse);
                        System.out.println("✅ Client créé. ID Client = " + idClient);
                    } catch (Exception e) {
                        printError(e);
                    }
                }
                case 0 -> {
                    return; // logout
                }
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    private static void menuGestionnaire(Session session) {
        GestionnaireService service = new GestionnaireService(session);

        while (true) {
            System.out.println();
            System.out.println("===== MENU GESTIONNAIRE =====");
            System.out.println("1) Ajouter médicament");
            System.out.println("2) Mettre à jour stock (valeur exacte)");
            System.out.println("3) Afficher stock critique");
            System.out.println("4) Créer commande (réapprovisionnement)");
            System.out.println("5) Consulter commandes");
            System.out.println("6) Historique stock d’un médicament");
            System.out.println("7) Lister tous les médicaments");
            System.out.println("8) Modifier prix d’un médicament");
            System.out.println("0) Logout");

            int choix = readInt("Choix: ");

            switch (choix) {
                case 1 -> {
                    try {
                        String nom = readLine("Nom: ");
                        String dosage = readLine("Dosage: ");
                        int stock = readInt("Stock initial: ");
                        double prix = readDouble("Prix unitaire: ");
                        int id = service.ajouterMedicament(nom, dosage, stock, prix);
                        System.out.println("✅ Médicament ajouté. ID = " + id);
                    } catch (Exception e) {
                        printError(e);
                    }
                }
                case 2 -> {
                    try {
                        int idMed = readInt("ID medicament: ");
                        int stock = readInt("Nouveau stock: ");
                        service.mettreAJourStockExact(idMed, stock);
                        System.out.println("✅ Stock mis à jour.");
                    } catch (Exception e) {
                        printError(e);
                    }
                }
                case 3 -> {
                    try {
                        int seuil = readInt("Seuil critique (ex: 5): ");
                        List<MedicamentDao.Medicament> list = service.stockCritique(seuil);
                        if (list.isEmpty()) {
                            System.out.println("(Aucun médicament en stock critique)");
                        } else {
                            for (MedicamentDao.Medicament m : list) {
                                System.out.println("Med#" + m.id()
                                        + " | " + m.nom()
                                        + " | " + m.dosage()
                                        + " | stock=" + m.stock()
                                        + " | prix=" + m.prixUnitaire());
                            }
                        }
                    } catch (Exception e) {
                        printError(e);
                    }
                }
                case 4 -> {
                    try {
                        int idMed = readInt("ID medicament: ");
                        int qte = readInt("Quantité: ");
                        int idCmd = service.creerCommande(idMed, qte);
                        System.out.println("✅ Commande créée. ID = " + idCmd);
                    } catch (Exception e) {
                        printError(e);
                    }
                }
                case 5 -> {
                    try {
                        List<CommandeDao.Commande> cmds = service.consulterCommandes();
                        if (cmds.isEmpty()) {
                            System.out.println("(Aucune commande)");
                        } else {
                            for (CommandeDao.Commande c : cmds) {
                                System.out.println("Cmd#" + c.id()
                                        + " | med=" + c.idMedicament()
                                        + " | qte=" + c.quantite()
                                        + " | date=" + c.date());
                            }
                        }
                    } catch (Exception e) {
                        printError(e);
                    }
                }
                case 6 -> {
                    try {
                        int idMed = readInt("ID medicament: ");
                        List<StockHistoriqueDao.StockHist> hist = service.historiqueStock(idMed);
                        if (hist.isEmpty()) {
                            System.out.println("(Historique vide)");
                        } else {
                            for (StockHistoriqueDao.StockHist h : hist) {
                                System.out.println("Hist#" + h.id()
                                        + " | med=" + h.idMedicament()
                                        + " | delta=" + h.quantite()
                                        + " | date=" + h.date());
                            }
                        }
                    } catch (Exception e) {
                        printError(e);
                    }
                }
                case 7 -> {
                    try {
                        List<MedicamentDao.Medicament> meds = service.listMedicaments();
                        if (meds.isEmpty()) {
                            System.out.println("(Aucun médicament)");
                        } else {
                            for (MedicamentDao.Medicament m : meds) {
                                System.out.println("Med#" + m.id()
                                        + " | " + m.nom()
                                        + " | " + m.dosage()
                                        + " | stock=" + m.stock()
                                        + " | prix=" + m.prixUnitaire());
                            }
                        }
                    } catch (Exception e) {
                        printError(e);
                    }
                }
                case 8 -> {
                    try {
                        int idMed = readInt("ID medicament: ");
                        double prix = readDouble("Nouveau prix: ");
                        service.modifierPrix(idMed, prix);
                        System.out.println("✅ Prix modifié.");
                    } catch (Exception e) {
                        printError(e);
                    }
                }
                case 0 -> {
                    return; // logout
                }
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    // ---------------------- Helpers ----------------------

    private static String readLine(String msg) {
        System.out.print(msg);
        return sc.nextLine().trim();
    }

    private static int readInt(String msg) {
        while (true) {
            System.out.print(msg);
            String s = sc.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (Exception ignored) {
                System.out.println("Entrez un nombre valide.");
            }
        }
    }

    private static double readDouble(String msg) {
        while (true) {
            System.out.print(msg);
            String s = sc.nextLine().trim();
            try {
                return Double.parseDouble(s);
            } catch (Exception ignored) {
                System.out.println("Entrez un nombre (double) valide.");
            }
        }
    }

    private static void printError(Exception e) {
        System.out.println("❌ Erreur: " + e.getMessage());
    }
}

