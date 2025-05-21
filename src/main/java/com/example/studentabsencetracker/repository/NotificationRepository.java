package com.example.studentabsencetracker.repository;

import com.example.studentabsencetracker.model.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Notification> findByUserIdAndIsReadOrderByCreatedAtDesc(Long userId, Boolean isRead);
    long countByUserIdAndIsRead(Long userId, Boolean isRead);
}