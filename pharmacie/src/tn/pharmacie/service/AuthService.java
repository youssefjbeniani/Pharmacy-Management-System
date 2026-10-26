package tn.pharmacie.service;

// ===== IMPORTS =====
import tn.pharmacie.dao.AuthDao;      // DAO qui fait la vérification login/pwd dans la base (Pharmacien/Gestionnaire)
import tn.pharmacie.model.Session;    // Objet qui représente l'utilisateur connecté (rôle, id, nom, prénom)

/**
 * AuthService = couche "Service" pour l'authentification.
 * -----------------------------------------------
 * Le rôle d'un service :
 * - contenir la logique métier (business logic)
 * - appeler les DAO pour accéder à la base de données
 *
 * Ici, AuthService est très simple :
 * - il délègue l'authentification à AuthDao
 * - il renvoie une Session si l'utilisateur existe, sinon null
 */
public class AuthService {

    /**
     * Instance du DAO d'authentification.
     * final => la référence ne change pas.
     */
    private final AuthDao authDao = new AuthDao();

    /**
     * Méthode de login appelée par App.java.
     *
     * @param login login saisi par l'utilisateur
     * @param pwd mot de passe saisi par l'utilisateur
     * @return Session si login/pwd corrects (pharmacien ou gestionnaire), sinon null
     */
    public Session login(String login, String pwd) {

        // On délègue la vérification à AuthDao (accès base + requêtes SQL)
        return authDao.login(login, pwd);
    }
}
