package com.example.studentabsencetracker.repository;

import com.example.studentabsencetracker.model.entity.Justification;
import com.example.studentabsencetracker.model.entity.User;
import com.example.studentabsencetracker.model.enums.JustificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JustificationRepository extends JpaRepository<Justification, Long> {

    // Find justifications by submitted_by (student)
    List<Justification> findBySubmittedBy(User submittedBy);

    // Find justifications by submitted_by and status
    List<Justification> findBySubmittedByAndStatus(User submittedBy, JustificationStatus status);

    // Find justifications by status
    List<Justification> findByStatus(JustificationStatus status);

    // Find justifications for a specific absence
    Optional<Justification> findByAbsenceId(Long absenceId);

    // Find justifications that need review by a specific teacher
    @Query("SELECT j FROM Justification j WHERE j.absence.courseClass.teacher = :teacher AND j.status = :status")
    List<Justification> findByTeacherAndStatus(@Param("teacher") User teacher, @Param("status") JustificationStatus status);

    // Find all justifications for a teacher's classes
    @Query("SELECT j FROM Justification j WHERE j.absence.courseClass.teacher = :teacher")
    List<Justification> findByTeacher(@Param("teacher") User teacher);

    // Count pending justifications for a teacher
    @Query("SELECT COUNT(j) FROM Justification j WHERE j.absence.courseClass.teacher = :teacher AND j.status = 'PENDING'")
    long countPendingJustificationsByTeacher(@Param("teacher") User teacher);
}