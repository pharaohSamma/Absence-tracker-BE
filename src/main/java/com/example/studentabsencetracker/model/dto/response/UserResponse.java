package com.example.studentabsencetracker.model.dto.response;

import com.example.studentabsencetracker.model.entity.User;
import com.example.studentabsencetracker.model.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private RoleType role;
    private boolean isActive;
    private LocalDateTime createdAt;

    // Constructor that takes a User entity (for easy conversion)
    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.role = user.getRole();
        this.isActive = user.isActive();
        this.createdAt = user.getCreatedAt();
    }
}