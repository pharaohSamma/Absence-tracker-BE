package com.example.studentabsencetracker.model.dto.request;

import com.example.studentabsencetracker.model.enums.JustificationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JustificationReviewRequest {
    private JustificationStatus status; // APPROVED or REJECTED
    private String reviewNotes;
}