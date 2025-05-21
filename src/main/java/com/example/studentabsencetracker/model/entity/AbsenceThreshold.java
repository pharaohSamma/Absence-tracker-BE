package com.example.studentabsencetracker.model.entity;

import com.example.studentabsencetracker.model.enums.ThresholdType;
import com.example.studentabsencetracker.model.enums.ThresholdActionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "absence_thresholds")
public class AbsenceThreshold {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "threshold_type", nullable = false)
    private ThresholdType thresholdType;

    @Column(name = "threshold_count", nullable = false)
    private Integer thresholdCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ThresholdActionType actionType;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}