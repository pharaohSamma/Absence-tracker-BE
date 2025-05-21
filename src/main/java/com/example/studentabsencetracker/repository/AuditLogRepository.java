package com.example.studentabsencetracker.repository;

import com.example.studentabsencetracker.model.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);
}