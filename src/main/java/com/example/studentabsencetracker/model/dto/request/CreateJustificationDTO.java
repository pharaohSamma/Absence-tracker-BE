package com.example.studentabsencetracker.model.dto.request;


import com.example.studentabsencetracker.model.enums.JustificationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// DTO for creating a new justification
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateJustificationDTO {

    @NotNull(message = "Absence ID is required")
    private Long absenceId;

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    private String documentUrl;
}