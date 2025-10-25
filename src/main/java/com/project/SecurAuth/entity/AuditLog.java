package com.project.SecurAuth.entity;

import com.project.SecurAuth.entity.Enum.AuditStatus;
import com.project.SecurAuth.entity.Enum.SeverityLevel;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String username;

    private String action;

    private String description;

    private String ipAddress;

    @Enumerated(EnumType.STRING)
    private AuditStatus status;

    @Enumerated(EnumType.STRING)
    private SeverityLevel severity;

    @CreationTimestamp
    private LocalDateTime timestamp;

    private String performedBy;


}
