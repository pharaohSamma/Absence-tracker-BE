package com.example.studentabsencetracker.service;

import com.example.studentabsencetracker.exception.BadRequestException;
import com.example.studentabsencetracker.exception.ResourceNotFoundException;
import com.example.studentabsencetracker.exception.UnauthorizedException;
import com.example.studentabsencetracker.model.dto.request.JustificationRequest;
import com.example.studentabsencetracker.model.dto.request.JustificationReviewRequest;
import com.example.studentabsencetracker.model.dto.response.JustificationResponse;
import com.example.studentabsencetracker.model.dto.response.JustificationStatistics;
import com.example.studentabsencetracker.model.entity.Absence;
import com.example.studentabsencetracker.model.entity.Justification;
import com.example.studentabsencetracker.model.entity.User;
import com.example.studentabsencetracker.model.enums.JustificationStatus;
import com.example.studentabsencetracker.model.enums.RoleType;
import com.example.studentabsencetracker.repository.AbsenceRepository;
import com.example.studentabsencetracker.repository.JustificationRepository;
import com.example.studentabsencetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class JustificationService {

    @Autowired
    private JustificationRepository justificationRepository;

    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private UserRepository userRepository;

    // Step 3.1: Justification Submission Process for Students
    public JustificationResponse submitJustification(JustificationRequest request) {
        // Get current authenticated user
        User currentUser = getCurrentUser();

        // Verify user is a student or parent
        if (currentUser.getRole() != RoleType.STUDENT && currentUser.getRole() != RoleType.PARENT) {
            throw new UnauthorizedException("Only students or parents can submit justifications");
        }

        // Find the absence
        Absence absence = absenceRepository.findById(request.getAbsenceId())
                .orElseThrow(() -> new ResourceNotFoundException("Absence not found with id: " + request.getAbsenceId()));

        // Verify the user has permission to justify this absence
        if (currentUser.getRole() == RoleType.STUDENT) {
            if (!absence.getStudent().getId().equals(currentUser.getId())) {
                throw new UnauthorizedException("You can only justify your own absences");
            }
        } else if (currentUser.getRole() == RoleType.PARENT) {
            // Check if current user is parent of the student
            // Note: You may need to implement the parent-child relationship in User entity
            // For now, we'll skip this check or you can add the relationship

            boolean isParent = currentUser.getChildren().stream()
                    .anyMatch(child -> child.getId().equals(absence.getStudent().getId()));
            if (!isParent) {
                throw new UnauthorizedException("You can only justify absences for your children");
            }

        }

        // Check if justification already exists
        if (justificationRepository.findByAbsenceId(request.getAbsenceId()).isPresent()) {
            throw new BadRequestException("Justification already exists for this absence");
        }

        // Create new justification
        Justification justification = new Justification();
        justification.setAbsence(absence);
        justification.setSubmittedBy(currentUser);
        justification.setDescription(request.getDescription());
        justification.setDocumentUrl(request.getDocumentUrl());
        justification.setStatus(JustificationStatus.PENDING);

        justification = justificationRepository.save(justification);

        return new JustificationResponse(justification);
    }

    // Get justifications for current user
    public List<JustificationResponse> getMyJustifications() {
        User currentUser = getCurrentUser();
        List<Justification> justifications;

        if (currentUser.getRole() == RoleType.STUDENT) {
            justifications = justificationRepository.findBySubmittedBy(currentUser);
        } else if (currentUser.getRole() == RoleType.PARENT) {
            // Get justifications submitted by this parent
            justifications = justificationRepository.findBySubmittedBy(currentUser);
        } else {
            throw new UnauthorizedException("Invalid role for accessing justifications");
        }

        return justifications.stream()
                .map(JustificationResponse::new)
                .collect(Collectors.toList());
    }

    // Step 3.2: Review System for Teachers
    public List<JustificationResponse> getPendingJustificationsForTeacher() {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() != RoleType.TEACHER) {
            throw new UnauthorizedException("Only teachers can review justifications");
        }

        List<Justification> pendingJustifications = justificationRepository
                .findByTeacherAndStatus(currentUser, JustificationStatus.PENDING);

        return pendingJustifications.stream()
                .map(JustificationResponse::new)
                .collect(Collectors.toList());
    }

    public List<JustificationResponse> getAllJustificationsForTeacher() {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() != RoleType.TEACHER) {
            throw new UnauthorizedException("Only teachers can view justifications");
        }

        List<Justification> justifications = justificationRepository.findByTeacher(currentUser);

        return justifications.stream()
                .map(JustificationResponse::new)
                .collect(Collectors.toList());
    }

    // Step 3.3: Process justification review and update absence status
    public JustificationResponse reviewJustification(Long justificationId, JustificationReviewRequest reviewRequest) {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() != RoleType.TEACHER) {
            throw new UnauthorizedException("Only teachers can review justifications");
        }

        Justification justification = justificationRepository.findById(justificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Justification not found with id: " + justificationId));

        // Verify teacher has permission to review this justification
        if (!justification.getAbsence().getCourseClass().getTeacher().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only review justifications for your classes");
        }

        // Verify justification is still pending
        if (justification.getStatus() != JustificationStatus.PENDING) {
            throw new BadRequestException("Justification has already been reviewed");
        }

        // Validate review status
        if (reviewRequest.getStatus() != JustificationStatus.APPROVED &&
                reviewRequest.getStatus() != JustificationStatus.REJECTED) {
            throw new BadRequestException("Invalid review status. Must be APPROVED or REJECTED");
        }

        // Update justification
        justification.setStatus(reviewRequest.getStatus());
        justification.setReviewNotes(reviewRequest.getReviewNotes());
        justification.setReviewedBy(currentUser);
        justification.setReviewedAt(LocalDateTime.now());

        justification = justificationRepository.save(justification);

        // Step 3.3: Update absence status based on justification approval
        updateAbsenceStatus(justification);

        return new JustificationResponse(justification);
    }

    // Step 3.3: Update absence status when justification is processed
    private void updateAbsenceStatus(Justification justification) {
        Absence absence = justification.getAbsence();

        if (justification.getStatus() == JustificationStatus.APPROVED) {
            // Mark absence as excused
            absence.setExcused(true);
            absence.setReason("Excused - Justification approved: " + justification.getDescription());
        } else if (justification.getStatus() == JustificationStatus.REJECTED) {
            // Keep absence as unexcused
            absence.setExcused(false);
            absence.setReason("Unexcused - Justification rejected: " +
                    (justification.getReviewNotes() != null ? justification.getReviewNotes() : "No reason provided"));
        }

        absenceRepository.save(absence);
    }

    // Get justification by ID (with permission check)
    public JustificationResponse getJustificationById(Long id) {
        User currentUser = getCurrentUser();

        Justification justification = justificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Justification not found with id: " + id));

        // Permission check based on role
        boolean hasPermission = false;
        switch (currentUser.getRole()) {
            case STUDENT:
                hasPermission = justification.getSubmittedBy().getId().equals(currentUser.getId()) ||
                        justification.getAbsence().getStudent().getId().equals(currentUser.getId());
                break;
            case PARENT:
                hasPermission = justification.getSubmittedBy().getId().equals(currentUser.getId());
                // Note: Add parent-child relationship check here if implemented
                break;
            case TEACHER:
                hasPermission = justification.getAbsence().getCourseClass().getTeacher().getId().equals(currentUser.getId());
                break;
            case ADMIN:
                hasPermission = true;
                break;
        }

        if (!hasPermission) {
            throw new UnauthorizedException("You don't have permission to view this justification");
        }

        return new JustificationResponse(justification);
    }

    // Get statistics for dashboard
    public Long getPendingJustificationsCount() {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() == RoleType.TEACHER) {
            return justificationRepository.countPendingJustificationsByTeacher(currentUser);
        }

        return 0L;
    }

    // Admin functionality - Get all justifications in the system
    public List<JustificationResponse> getAllJustifications() {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() != RoleType.ADMIN) {
            throw new UnauthorizedException("Only administrators can view all justifications");
        }

        List<Justification> allJustifications = justificationRepository.findAll();

        return allJustifications.stream()
                .map(JustificationResponse::new)
                .collect(Collectors.toList());
    }

    // Admin functionality - Get justifications by status
    public List<JustificationResponse> getJustificationsByStatus(JustificationStatus status) {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() != RoleType.ADMIN) {
            throw new UnauthorizedException("Only administrators can filter justifications by status");
        }

        List<Justification> justifications = justificationRepository.findByStatus(status);

        return justifications.stream()
                .map(JustificationResponse::new)
                .collect(Collectors.toList());
    }

    // Admin functionality - Get system statistics
    public JustificationStatistics getJustificationStatistics() {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() != RoleType.ADMIN) {
            throw new UnauthorizedException("Only administrators can view system statistics");
        }

        long totalJustifications = justificationRepository.count();
        long pendingCount = justificationRepository.findByStatus(JustificationStatus.PENDING).size();
        long approvedCount = justificationRepository.findByStatus(JustificationStatus.APPROVED).size();
        long rejectedCount = justificationRepository.findByStatus(JustificationStatus.REJECTED).size();

        return new JustificationStatistics(totalJustifications, pendingCount, approvedCount, rejectedCount);
    }

    // Helper method to get current authenticated user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }
}