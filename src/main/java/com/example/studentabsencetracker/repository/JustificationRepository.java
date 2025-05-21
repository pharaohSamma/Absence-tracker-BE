package com.example.studentabsencetracker.repository;

import com.example.studentabsencetracker.model.entity.Justification;
import com.example.studentabsencetracker.model.enums.JustificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JustificationRepository extends JpaRepository<Justification, Long> {
    Optional<Justification> findByAbsenceId(Long absenceId);
    List<Justification> findBySubmittedById(Long userId);
    List<Justification> findByStatus(JustificationStatus status);
}