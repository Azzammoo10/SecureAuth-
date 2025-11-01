package com.project.SecurAuth.service.interfaces;

import com.project.SecurAuth.entity.AuditLog;
import com.project.SecurAuth.entity.Enum.SeverityLevel;
import com.project.SecurAuth.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditService {

    /* ---------- LOGGING DE BASE ---------- */
    void logEvent(User user, String action, String details, SeverityLevel severity);

    /* ---------- CONSULTATION SELON LE RÔLE ---------- */

    // USER : consulter uniquement ses propres logs
    List<AuditLog> getMyLogs(String username);

    // MANAGER : consulter les logs des membres de son équipe
    List<AuditLog> getTeamLogs(String managerUsername);

    // ADMIN : supervision complète
    List<AuditLog> getAllLogs();
    void deleteOldLogs(int days);
    byte[] exportAuditReport(String format); // PDF, CSV…

    // SECURITY OFFICER : analyse et détection avancée
    List<AuditLog> getSuspiciousActivities();
    List<AuditLog> analyzeLoginPatterns(LocalDateTime start, LocalDateTime end);

    /* ---------- FILTRES GÉNÉRIQUES ---------- */

    List<AuditLog> getLogsByUser(String username);
    List<AuditLog> getLogsBySeverity(SeverityLevel level);
    List<AuditLog> getLogsBetweenDates(LocalDateTime start, LocalDateTime end);
}
