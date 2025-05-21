package com.example.studentabsencetracker.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "absences")
public class Absence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private CourseClass courseClass;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String reason;

    @Column(name = "is_excused")
    private Boolean excused = false;

    @ManyToOne
    @JoinColumn(name = "recorded_by")
    private User recordedBy;

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

    // Override the Lombok-generated methods to ensure proper naming
    public Boolean isExcused() {
        return excused;
    }

    public void setExcused(Boolean excused) {
        this.excused = excused;
    }
}