package com.example.studentabsencetracker.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbsenceRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Class ID is required")
    private Long classId;

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Absence date cannot be in the future")
    private LocalDate date;

    @NotNull(message = "Reason is required")
    @Size(min = 3, max = 255, message = "Reason must be between 3 and 255 characters")
    private String reason;

    private boolean excused = false;
}