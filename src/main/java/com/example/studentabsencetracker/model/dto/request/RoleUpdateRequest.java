package com.example.studentabsencetracker.model.dto.request;

import com.example.studentabsencetracker.model.enums.RoleType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleUpdateRequest {
    @NotNull(message = "Role is required")
    private RoleType role;
}