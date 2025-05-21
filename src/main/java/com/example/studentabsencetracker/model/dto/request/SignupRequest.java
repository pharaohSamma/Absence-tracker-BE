package com.example.studentabsencetracker.model.dto.request;

import com.example.studentabsencetracker.model.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private RoleType role;
}