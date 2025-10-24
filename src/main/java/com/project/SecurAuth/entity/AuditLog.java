package com.project.SecurAuth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class AuditLog {
    @Id
    @GeneratedValue
    private Long id;

    private String userName;

    private String action;

    private String description;

    private String ipAddress;

    private String status;

    private String timestamp;


}
