package com.example.studentabsencetracker.model.dto.response;

import com.example.studentabsencetracker.model.entity.Justification;
import com.example.studentabsencetracker.model.enums.JustificationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JustificationResponse {
    private Long id;
    private Long absenceId;
    private LocalDate absenceDate;
    private String className;
    private String submittedByUsername;
    private String description;
    private String documentUrl;
    private JustificationStatus status;
    private String reviewNotes;
    private String reviewedByUsername;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;

    public JustificationResponse(Justification justification) {
        this.id = justification.getId();
        this.absenceId = justification.getAbsence().getId();
        this.absenceDate = justification.getAbsence().getDate();
        this.className = justification.getAbsence().getCourseClass().getName();
        this.submittedByUsername = justification.getSubmittedBy().getUsername();
        this.description = justification.getDescription();
        this.documentUrl = justification.getDocumentUrl();
        this.status = justification.getStatus();
        this.reviewNotes = justification.getReviewNotes();
        this.reviewedByUsername = justification.getReviewedBy() != null ?
                justification.getReviewedBy().getUsername() : null;
        this.reviewedAt = justification.getReviewedAt();
        this.createdAt = justification.getCreatedAt();
    }
}