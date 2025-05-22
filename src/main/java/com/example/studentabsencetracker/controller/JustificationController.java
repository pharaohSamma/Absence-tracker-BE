package com.example.studentabsencetracker.controller;

import com.example.studentabsencetracker.model.dto.request.JustificationRequest;
import com.example.studentabsencetracker.model.dto.request.JustificationReviewRequest;
import com.example.studentabsencetracker.model.dto.response.JustificationResponse;
import com.example.studentabsencetracker.model.dto.response.JustificationStatistics;
import com.example.studentabsencetracker.model.enums.JustificationStatus;
import com.example.studentabsencetracker.service.JustificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/justifications")
public class JustificationController {

    @Autowired
    private JustificationService justificationService;

    // Test endpoint (keep for debugging)
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Justification controller is working!");
    }

    // Step 3.1: Student/Parent Justification Submission
    @PostMapping("/submit")
    @PreAuthorize("hasRole('STUDENT') or hasRole('PARENT')")
    public ResponseEntity<JustificationResponse> submitJustification(@RequestBody JustificationRequest request) {
        JustificationResponse response = justificationService.submitJustification(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/debug/current-user")
    public ResponseEntity<String> debugCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok("Current user: " + auth.getName() + ", Authorities: " + auth.getAuthorities());
    }
    // Get my justifications (for students/parents)
    @GetMapping("/my-justifications")
    @PreAuthorize("hasRole('STUDENT') or hasRole('PARENT')")
    public ResponseEntity<List<JustificationResponse>> getMyJustifications() {
        List<JustificationResponse> justifications = justificationService.getMyJustifications();
        return ResponseEntity.ok(justifications);
    }

    // Step 3.2: Teacher Review System - Get pending justifications
    @GetMapping("/pending")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<JustificationResponse>> getPendingJustifications() {
        List<JustificationResponse> pendingJustifications = justificationService.getPendingJustificationsForTeacher();
        return ResponseEntity.ok(pendingJustifications);
    }

    // Get all justifications for teacher's classes
    @GetMapping("/teacher/all")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<JustificationResponse>> getAllJustificationsForTeacher() {
        List<JustificationResponse> justifications = justificationService.getAllJustificationsForTeacher();
        return ResponseEntity.ok(justifications);
    }

    // Step 3.2 & 3.3: Teacher Reviews Justification (Approve/Reject)
    @PutMapping("/{justificationId}/review")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<JustificationResponse> reviewJustification(
            @PathVariable Long justificationId,
            @RequestBody JustificationReviewRequest reviewRequest) {
        JustificationResponse response = justificationService.reviewJustification(justificationId, reviewRequest);
        return ResponseEntity.ok(response);
    }

    // Get specific justification by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('PARENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<JustificationResponse> getJustificationById(@PathVariable Long id) {
        JustificationResponse justification = justificationService.getJustificationById(id);
        return ResponseEntity.ok(justification);
    }

    // Get pending count for dashboard
    @GetMapping("/pending/count")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Long> getPendingJustificationsCount() {
        Long count = justificationService.getPendingJustificationsCount();
        return ResponseEntity.ok(count);
    }

    // Admin endpoints
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<JustificationResponse>> getAllJustifications() {
        List<JustificationResponse> justifications = justificationService.getAllJustifications();
        return ResponseEntity.ok(justifications);
    }

    // Get justifications by status (Admin only)
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<JustificationResponse>> getJustificationsByStatus(@PathVariable JustificationStatus status) {
        List<JustificationResponse> justifications = justificationService.getJustificationsByStatus(status);
        return ResponseEntity.ok(justifications);
    }

    // Get system statistics (Admin only)
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<JustificationStatistics> getJustificationStatistics() {
        JustificationStatistics statistics = justificationService.getJustificationStatistics();
        return ResponseEntity.ok(statistics);
    }
}
