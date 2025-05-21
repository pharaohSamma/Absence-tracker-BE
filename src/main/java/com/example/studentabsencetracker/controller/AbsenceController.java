package com.example.studentabsencetracker.controller;

import com.example.studentabsencetracker.model.dto.request.AbsenceRequest;
import com.example.studentabsencetracker.model.dto.response.AbsenceResponse;
import com.example.studentabsencetracker.service.AbsenceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/absences")
public class AbsenceController {

    @Autowired
    private AbsenceService absenceService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AbsenceResponse>> getAllAbsences() {
        return ResponseEntity.ok(absenceService.getAllAbsences());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<AbsenceResponse> getAbsenceById(@PathVariable Long id) {
        return ResponseEntity.ok(absenceService.getAbsenceById(id));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER') or (hasRole('STUDENT') and #studentId == principal.id) or hasRole('PARENT')")
    public ResponseEntity<List<AbsenceResponse>> getAbsencesByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(absenceService.getAbsencesByStudent(studentId));
    }

    @GetMapping("/student/{studentId}/dateRange")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER') or (hasRole('STUDENT') and #studentId == principal.id) or hasRole('PARENT')")
    public ResponseEntity<List<AbsenceResponse>> getAbsencesByStudentAndDateRange(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(absenceService.getAbsencesByStudentAndDateRange(studentId, startDate, endDate));
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<AbsenceResponse>> getAbsencesByClass(@PathVariable Long classId) {
        return ResponseEntity.ok(absenceService.getAbsencesByClass(classId));
    }

    @GetMapping("/class/{classId}/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<AbsenceResponse>> getAbsencesByClassAndDate(
            @PathVariable Long classId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(absenceService.getAbsencesByClassAndDate(classId, date));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<AbsenceResponse> createAbsence(@Valid @RequestBody AbsenceRequest absenceRequest) {
        return new ResponseEntity<>(absenceService.createAbsence(absenceRequest), HttpStatus.CREATED);
    }

    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<AbsenceResponse>> createAbsencesBatch(@Valid @RequestBody List<AbsenceRequest> absenceRequests) {
        return new ResponseEntity<>(absenceService.createAbsencesBatch(absenceRequests), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<AbsenceResponse> updateAbsence(
            @PathVariable Long id,
            @Valid @RequestBody AbsenceRequest absenceRequest) {
        return ResponseEntity.ok(absenceService.updateAbsence(id, absenceRequest));
    }

    @PatchMapping("/{id}/excuse")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<AbsenceResponse> markAsExcused(@PathVariable Long id) {
        return ResponseEntity.ok(absenceService.markAsExcused(id));
    }

    @PatchMapping("/{id}/unexcuse")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<AbsenceResponse> markAsUnexcused(@PathVariable Long id) {
        return ResponseEntity.ok(absenceService.markAsUnexcused(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Void> deleteAbsence(@PathVariable Long id) {
        absenceService.deleteAbsence(id);
        return ResponseEntity.noContent().build();
    }
}