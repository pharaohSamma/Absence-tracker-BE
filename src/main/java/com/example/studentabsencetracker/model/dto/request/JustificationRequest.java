package com.example.studentabsencetracker.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JustificationRequest {
    private Long absenceId;
    private String description;
    private String documentUrl; // Optional - for file uploads
}