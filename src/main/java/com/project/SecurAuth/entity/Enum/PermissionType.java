package com.project.SecurAuth.entity.Enum;


public enum PermissionType {

    /* ================= AUTHENTIFICATION ================= */
    AUTH_LOGIN("AUTH_LOGIN"),                    // Se connecter
    AUTH_REFRESH_TOKEN("AUTH_REFRESH_TOKEN"),    // Rafraîchir le token
    AUTH_LOGOUT("AUTH_LOGOUT"),                  // Se déconnecter
    AUTH_FORCE_LOGOUT("AUTH_FORCE_LOGOUT"),      // Forcer la déconnexion (admin)
    AUTH_2FA_VERIFY("AUTH_2FA_VERIFY"),          // Vérifier code 2FA

    /* ================= UTILISATEURS ================= */
    USER_CREATE("USER_CREATE"),                  // Créer un utilisateur
    USER_UPDATE("USER_UPDATE"),                  // Modifier un utilisateur
    USER_DELETE("USER_DELETE"),                  // Supprimer un utilisateur
    USER_VIEW("USER_VIEW"),                      // Voir tous les utilisateurs
    USER_VIEW_SELF("USER_VIEW_SELF"),            // Voir ses propres infos
    USER_ENABLE("USER_ENABLE"),                  // Activer un utilisateur
    USER_DISABLE("USER_DISABLE"),                // Désactiver un utilisateur
    USER_LOCK("USER_LOCK"),                      // Bloquer un compte
    USER_UNLOCK("USER_UNLOCK"),                  // Débloquer un compte
    USER_RESET_PASSWORD("USER_RESET_PASSWORD"),  // Réinitialiser un mot de passe
    USER_MANAGE_TEAM("USER_MANAGE_TEAM"),        // Gérer les membres de son équipe

    /* ================= RÔLES ================= */
    ROLE_CREATE("ROLE_CREATE"),                  // Créer un rôle
    ROLE_UPDATE("ROLE_UPDATE"),                  // Modifier un rôle
    ROLE_DELETE("ROLE_DELETE"),                  // Supprimer un rôle
    ROLE_VIEW("ROLE_VIEW"),                      // Voir tous les rôles
    ROLE_ASSIGN("ROLE_ASSIGN"),                  // Assigner un rôle à un utilisateur
    ROLE_REMOVE("ROLE_REMOVE"),                  // Retirer un rôle à un utilisateur

    /* ================= AUDIT & JOURNALISATION ================= */
    AUDIT_VIEW_ALL("AUDIT_VIEW_ALL"),            // Voir tous les logs (admin / sécurité)
    AUDIT_VIEW_SELF("AUDIT_VIEW_SELF"),          // Voir ses propres logs
    AUDIT_VIEW_TEAM("AUDIT_VIEW_TEAM"),          // Voir les logs de son équipe
    AUDIT_EXPORT("AUDIT_EXPORT"),                // Exporter un rapport d’audit
    AUDIT_DELETE_OLD("AUDIT_DELETE_OLD"),        // Supprimer les anciens logs
    AUDIT_ANALYZE("AUDIT_ANALYZE"),              // Analyser les comportements suspects

    /* ================= SECURITÉ & ALERTES ================= */
    SECURITY_ALERT_VIEW("SECURITY_ALERT_VIEW"),  // Voir les alertes sécurité
    SECURITY_ALERT_SEND("SECURITY_ALERT_SEND"),  // Envoyer une alerte sécurité
    SECURITY_IP_BLOCK("SECURITY_IP_BLOCK"),      // Bloquer une adresse IP
    SECURITY_IP_UNBLOCK("SECURITY_IP_UNBLOCK"),  // Débloquer une IP
    SECURITY_SUSPICIOUS_MONITOR("SECURITY_SUSPICIOUS_MONITOR"); // Surveiller les activités suspectes


    private final String value;

    PermissionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
