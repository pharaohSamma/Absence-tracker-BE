package com.example.studentabsencetracker.repository;

import com.example.studentabsencetracker.model.entity.AbsenceThreshold;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AbsenceThresholdRepository extends JpaRepository<AbsenceThreshold, Long> {
    List<AbsenceThreshold> findByIsActiveTrue();
}