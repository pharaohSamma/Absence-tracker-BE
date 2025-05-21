package com.example.studentabsencetracker.model.entity;

import com.example.studentabsencetracker.model.enums.JustificationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "justifications")
public class Justification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "absence_id", nullable = false)
    private Absence absence;

    @ManyToOne
    @JoinColumn(name = "submitted_by", nullable = false)
    private User submittedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JustificationStatus status = JustificationStatus.PENDING;

    @Column(nullable = false)
    private String description;

    @Column(name = "document_url")
    private String documentUrl;

    @ManyToOne
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Column(name = "review_notes")
    private String reviewNotes;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

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