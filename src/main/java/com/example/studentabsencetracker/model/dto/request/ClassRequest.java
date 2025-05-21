// ClassRequest.java
package com.example.studentabsencetracker.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassRequest {
    @NotBlank(message = "Class code is required")
    private String classCode;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    private Long teacherId;  // Optional initially, can be assigned later
}