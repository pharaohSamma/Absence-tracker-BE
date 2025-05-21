package com.example.studentabsencetracker.controller;

import com.example.studentabsencetracker.model.dto.request.PasswordUpdateRequest;
import com.example.studentabsencetracker.model.dto.request.RoleUpdateRequest;
import com.example.studentabsencetracker.model.dto.request.UserRequest;
import com.example.studentabsencetracker.model.dto.response.PagedUserResponse;
import com.example.studentabsencetracker.model.dto.response.UserResponse;
import com.example.studentabsencetracker.model.enums.RoleType;
import com.example.studentabsencetracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.studentabsencetracker.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Keep existing methods...

    // Replace the existing getAllUsers method with this paginated version
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedUserResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(userService.getAllUsers(pageNo, pageSize, sortBy, sortDir));
    }

    // Add these methods to your existing UserController.java

    // Paginated getAllUsers - replace or overload your existing getAllUsers method
    @GetMapping("/paginated")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedUserResponse> getAllUsersPaginated(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PagedUserResponse pagedResponse = userService.getAllUsers(pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(pagedResponse);
    }

    // Search endpoint
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<PagedUserResponse> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        PagedUserResponse searchResults = userService.searchUsers(query, pageNo, pageSize);
        return ResponseEntity.ok(searchResults);
    }

    // Password update
    @PatchMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<UserResponse> updatePassword(
            @PathVariable Long id,
            @Valid @RequestBody PasswordUpdateRequest passwordUpdateRequest) {
        UserResponse updatedUser = userService.updatePassword(id, passwordUpdateRequest);
        return ResponseEntity.ok(updatedUser);
    }

    // Role update
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequest roleUpdateRequest) {
        UserResponse updatedUser = userService.updateRole(id, roleUpdateRequest);
        return ResponseEntity.ok(updatedUser);
    }

    // Role-specific endpoints
    @PostMapping("/teachers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createTeacher(@Valid @RequestBody UserRequest userRequest) {
        userRequest.setRole(RoleType.TEACHER);
        UserResponse teacher = userService.createUser(userRequest);
        return ResponseEntity.ok(teacher);
    }

    @PostMapping("/students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createStudent(@Valid @RequestBody UserRequest userRequest) {
        userRequest.setRole(RoleType.STUDENT);
        UserResponse student = userService.createUser(userRequest);
        return ResponseEntity.ok(student);
    }

    @PostMapping("/parents")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createParent(@Valid @RequestBody UserRequest userRequest) {
        userRequest.setRole(RoleType.PARENT);
        UserResponse parent = userService.createUser(userRequest);
        return ResponseEntity.ok(parent);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();

        Map<String, String> response = new HashMap<>();
        response.put("username", username);

        return ResponseEntity.ok(response);
    }

}