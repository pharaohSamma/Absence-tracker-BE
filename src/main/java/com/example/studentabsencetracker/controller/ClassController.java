package com.example.studentabsencetracker.controller;

import com.example.studentabsencetracker.model.dto.request.ClassRequest;
import com.example.studentabsencetracker.model.dto.response.ClassResponse;
import com.example.studentabsencetracker.service.ClassService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/classes")
public class ClassController {

    @Autowired
    private ClassService classService;

    @GetMapping
    public ResponseEntity<List<ClassResponse>> getAllClasses() {
        return ResponseEntity.ok(classService.getAllClasses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassResponse> getClassById(@PathVariable Long id) {
        return ResponseEntity.ok(classService.getClassById(id));
    }

    @GetMapping("/code/{classCode}")
    public ResponseEntity<ClassResponse> getClassByCode(@PathVariable String classCode) {
        return ResponseEntity.ok(classService.getClassByCode(classCode));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<ClassResponse>> getClassesByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(classService.getClassesByTeacher(teacherId));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ClassResponse>> getClassesByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(classService.getClassesByStudent(studentId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClassResponse> createClass(@Valid @RequestBody ClassRequest classRequest) {
        return ResponseEntity.ok(classService.createClass(classRequest));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClassResponse> updateClass(
            @PathVariable Long id,
            @Valid @RequestBody ClassRequest classRequest) {
        return ResponseEntity.ok(classService.updateClass(id, classRequest));
    }

    @PutMapping("/{classId}/teacher/{teacherId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClassResponse> assignTeacher(
            @PathVariable Long classId,
            @PathVariable Long teacherId) {
        return ResponseEntity.ok(classService.assignTeacher(classId, teacherId));
    }

    @PutMapping("/{classId}/students/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ClassResponse> enrollStudent(
            @PathVariable Long classId,
            @PathVariable Long studentId) {
        return ResponseEntity.ok(classService.enrollStudent(classId, studentId));
    }

    @DeleteMapping("/{classId}/students/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ClassResponse> removeStudent(
            @PathVariable Long classId,
            @PathVariable Long studentId) {
        return ResponseEntity.ok(classService.removeStudent(classId, studentId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        classService.deleteClass(id);
        return ResponseEntity.ok().build();
    }
}