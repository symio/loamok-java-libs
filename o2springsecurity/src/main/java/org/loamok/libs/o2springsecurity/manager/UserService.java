package org.loamok.libs.o2springsecurity.manager;

import org.loamok.libs.o2springsecurity.entity.User;

/**
 * Définition du manager d'utilisateur(s)
 *
 * @author Huby Franck
 */
public interface UserService {
    /**
     * Inscrit un utilisateur
     * 
     * @param u Utilisateur
     * @param toSave À enregistrer ? 
     * @return Utilisateur
     */
    User registerUser(User u, boolean toSave);
    /**
     * Inscrit un utilisateur (surcharge)
     * 
     * @param u Utilisateur
     * @param isAdmin Utilisateur en cours est admin ? (pas l'utilisateur à enregistrer)
     * @param toSave À enregistrer ? 
     * @return Utilisateur
     */
    User registerUser(User u, Boolean isAdmin, boolean toSave);
    /**
     * Désactive un Utilisateur valide
     * 
     * @param emailKey Clé de vérification Email
     * @param emailMessageKey Nom de clé de message e-mail
     * @return vrai ou faux
     */
    Boolean deactivateRegisteredUser(String emailKey, String emailMessageKey);
    /**
     * Active un Utilisateur enregistré non actif
     * 
     * @param emailKey Clé de vérification Email
     * @return vrai ou faux
     */
    Boolean activateRegisteredUser(String emailKey);
    /**
     * Première étape du challenge mot de passe perdu
     * Génère une clé de validation pour les étapes suivantes
     * Désactive temporairement l'utilisateur
     * Envoi un email pour (un choix sur deux) :
     *  - annuler la demande
     *  - valider et passer à l'étape 2
     * 
     * @param email Adresse e-mail (username) de l'utilisateu-r
     * @return vrai ou faux
     */
    User resetChallengeRegisteredUser1(String email);
    /**
     * Annule la demande de nouveau mot de passe
     * Réactive l'utilisateur
     * Supprime la clé de validation e-mail
     * Envoi des e-mails :
     *  - à l'utilisateur pour l'informer qu'il à annulé le challenge
     *  - à l'admin pour l'informer qu'un utilisateur à annulé le challenge
     * 
     * @param emailKey Clé de vérification Email
     * @param resetOnly Désactivation définitive ou non de l'utilisateur ? (mutualisation pour disableResetChallenge2)
     * @return vrai ou faux
     */
    Boolean disableResetChallenge1(String emailKey, Boolean resetOnly);
    /**
     * Étape 2 du challenge mot de passe perdu
     * Écrase le mot de passe de l'utilisateur
     * Génère une clé e-mail sans expiration pour l'annulation
     * Envoi un email pour :
     *  - informer l'utilisateur du changement effectif de mot de passe et de la réactivatin de son compte
     *  - propose l'annulation du challenge et la désactivation (blocage applicatif) du compte
     * 
     * @param emailKey Clé de vérification Email
     * @param newPassword Nouveau mot de passe
     * @param newPasswordVerification Confirmation du nouveau mot de passe
     * @return vrai ou faux
     */
    Boolean resetChallengeRegisteredUser2(String emailKey, String newPassword, String newPasswordVerification);
    /**
     * Annule la demande de nouveau mot de passe
     * désactive l'utilisateur et rends son compte inutilisable par l'appplication
     * Supprime la clé de validation e-mail
     * Envoi des e-mails :
     *  - à l'utilisateur pour l'informer qu'il à annulé le challenge et que son compte est désormais bloqué
     *  - à l'admin pour l'informer qu'un utilisateur à annulé le challenge et que son compte est désormais bloqué
     * 
     * @param emailKey Clé de vérification Email
     * @return vrai ou faux
     */
    Boolean disableResetChallenge2(String emailKey);
    /**
     * Lance les vérifications sur les données d'un utilisateur à enregistrer
     * 
     * @param u Utilisateur
     * @param failedValidation E/S contenant le nom de la/les contrainte(s) en échec
     * @return vrai ou faux
     */
    Boolean doCheckUserRegistering(User u, StringBuilder failedValidation);
    /**
     * Vérifie qu'une adresse e-mail est bien unique
     * CAD pas déjà utilisée par un autre utilisateur de l'application
     * 
     * @param email Adresse e-mail
     * @return vrai ou faux
     */
    Boolean checkEmailUnique(String email);
    /**
     * Vérifie la conformité du mot de passe
     * 
     * @param password Mot de passe
     * @return vrai ou faux
     */
    Boolean checkPasswordCorrect(String password);
    /**
     * Vérifie les champs non nullables
     * 
     * @param u Utilisateur
     * @param fieldName E/S liste du/des champ(s) vide(s)
     * @return vrai ou faux
     */
    Boolean checkNonNullFields(User u, StringBuilder fieldName);
}
