package com.example.studentabsencetracker.repository;

import com.example.studentabsencetracker.model.entity.EmailNotification;
import com.example.studentabsencetracker.model.enums.EmailStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailNotificationRepository extends JpaRepository<EmailNotification, Long> {
    List<EmailNotification> findByStatus(EmailStatus status);
}